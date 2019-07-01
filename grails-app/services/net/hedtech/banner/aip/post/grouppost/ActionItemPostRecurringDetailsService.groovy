/*******************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.aip.post.exceptions.ActionItemExceptionFactory
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.service.ServiceBase


import java.util.concurrent.TimeUnit
/**
 * ActionItemPost Recurring Details Service is responsible for initiating and processing recurring posts.
 */
@Slf4j
@Transactional
class ActionItemPostRecurringDetailsService extends ServiceBase {



    /**
     * validates user intpus
     * @param map map of user inputs
     */
    void preCreateValidate(map) {
        log.trace "Request map -{$map}"

        if (!map) {
            throw new ApplicationException(ActionItemPostService, new BusinessLogicValidationException('preCreate.validation.insufficient.request', []))
        }
        if (map && map.recurFrequency <= 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.frequency.greater.zero', []))
        }
        if (map && !map.recurFrequencyType) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.recurFrequencyType.invalid', []))
        }
        if (map && map.postingDispStartDays!=null && map.postingDispStartDays < 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.displayStartDateOffset.zero', []))
        }
        if (map && map.postingDispEndDays !=null && !map.postingDisplayEndDate && map.postingDispEndDays < 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.postingDispEndDays.zero', []))
        }
        if (map && map.postingDispEndDays!=null && !map.postingDisplayEndDate && map.postingDispEndDays < map.postingDispStartDays) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.postingDisplayEndDate.greater.postingDispStartDays', []))
        }
        if (map && map.postingDisplayEndDate && map.postingDisplayEndDate.compareTo(map.recurStartDate) < 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.postingDisplayEndDate.greater.than.recurStartDate', []))
        }
        if (map && map.postingDisplayEndDate && map.postingDisplayEndDate.compareTo(map.recurEndDate) < 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.postingDisplayEndDate.greater.than.recurEndDate', []))
        }
        if (map && map.recurFrequencyType == AIPConstants.RECURR_FREQUENCY_TYPE_DAYS && map.recurStartDate && !(map.recurEndDate.compareTo(map.recurStartDate) > 0)) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.recurStartDate.less.than.recurEndDate', []))
        }
        if (map && map.recurFrequencyType == AIPConstants.RECURR_FREQUENCY_TYPE_HOURS && map.recurEndDate && !(map.recurEndDate.compareTo(map.recurStartDate) >= 0)) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.recurStartDate.less.than.or.equals.recurEndDate', []))
        }

    }
    /**
     * Validates firm display end dates
     * @param actionItemPostRecurringDetails
     * @return
     */
    Boolean validateDisplayEndDate(ActionItemPostRecurringDetails actionItemPostRecurringDetails,Date displayStartDateTime ){
        if(!actionItemPostRecurringDetails.postingDispEndDays && actionItemPostRecurringDetails.postingDisplayEndDate ){
            Integer numberOfObjects = getNumberOfJobs(actionItemPostRecurringDetails,displayStartDateTime)
            Date postingDateOfLastJob = resolveScheduleDateTime(actionItemPostRecurringDetails,numberOfObjects-1)
            log.trace "validating display end date - Numbre of jobs - ${numberOfObjects},calculated date of last posting job -${postingDateOfLastJob},posting display end date -${actionItemPostRecurringDetails.postingDisplayEndDate}"
            if(removeTime(postingDateOfLastJob).compareTo(actionItemPostRecurringDetails.postingDisplayEndDate)>0){
                //if firm display end date chosen & calculated last posting job date < display end date
                //Display end date must be greater than or equal to date of last posting job.
                throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('validation.DispEndDate.greater.than.or.equals.dateLastPosting', []))
            }
        }
    }

    /**
     * Validates the first recurrance end date
     * @param actionItemPostRecurringDetails
     * @return
     */
    void validateRecurEndDate(ActionItemPostRecurringDetails actionItemPostRecurringDetails){
         //first recurrence job start date + start offset < recurrence end date if units is "days"
        if(actionItemPostRecurringDetails.recurFrequencyType==AIPConstants.RECURR_FREQUENCY_TYPE_DAYS){
            Integer daysToAdd = actionItemPostRecurringDetails.recurFrequency
            Date calculatedEndDate = addDays(actionItemPostRecurringDetails.recurStartDate,daysToAdd)
            if(calculatedEndDate.compareTo(actionItemPostRecurringDetails.recurEndDate)>0){
                throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('validation.firstJob.endDate.more.than.recurEndDate', []))
            }
        }

    }
    /**
     * validates the recurr stat date and time
     * @param actionItemPostRecurringDetails
     */
    void validateRecurrStartDateAndTime(ActionItemPostRecurringDetails actionItemPostRecurringDetails){
        Date now= new Date(System.currentTimeMillis())
        if (now.after(actionItemPostRecurringDetails.recurStartTime)) {
            throw ActionItemExceptionFactory.createApplicationException(ActionItemPostRecurringDetailsService, "validation.display.obsolete.recurStart.date")
        }

    }

    /**
     * Validate dates method calls various date validations required for recurrance
     * @param actionItemPostRecurringDetails
     */
    void validateDates(ActionItemPostRecurringDetails actionItemPostRecurringDetails,Date displayStartDateTime){
        validateDisplayEndDate(actionItemPostRecurringDetails,displayStartDateTime)
        validateRecurEndDate(actionItemPostRecurringDetails)
        validateRecurrStartDateAndTime(actionItemPostRecurringDetails)
    }

    /**
     * Calculates the number of that a recurring post creates
     * @param actionItemPostRecurringDetails metadata of recurring action item
     * @return
     */
    Long getNumberOfJobs(ActionItemPostRecurringDetails actionItemPostRecurringDetails, Date displayStartDateTime) {
        Long totalNumber = actionItemPostRecurringDetails.recurFrequencyType == AIPConstants.RECURR_FREQUENCY_TYPE_DAYS ? getDaysBetweenRecurStartAndEndDate(actionItemPostRecurringDetails)
                : getHoursBetweenRecurStartAndEndDate(actionItemPostRecurringDetails,displayStartDateTime)
        Long numberOfObjects = totalNumber / actionItemPostRecurringDetails.recurFrequency
        log.trace "Number of jobs for reccuring job id- ${actionItemPostRecurringDetails.id} is ${numberOfObjects}"
        numberOfObjects
    }

    /**
     * Calculates the days between recurrence start date and end date
     * @param actionItemPostRecurringDetails actionItemPostRecurringDetails containing details required for recurrence
     * @return
     */
    Long getDaysBetweenRecurStartAndEndDate(ActionItemPostRecurringDetails actionItemPostRecurringDetails) {
        Long diff = actionItemPostRecurringDetails.recurEndDate.getTime() - actionItemPostRecurringDetails.recurStartDate.getTime()
        TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }

    /**
     * Calculates the hours between recurrence start date and end date
     * @param actionItemPostRecurringDetails actionItemPostRecurringDetails containing details required for recurrence
     * @return
     */
    Long getHoursBetweenRecurStartAndEndDate(ActionItemPostRecurringDetails actionItemPostRecurringDetails,Date displayStartDateAndTime) {
        Calendar calculatedEndTime = Calendar.getInstance()
        calculatedEndTime.setTime(actionItemPostRecurringDetails.recurEndDate)
        calculatedEndTime.set(Calendar.HOUR_OF_DAY,23)
        calculatedEndTime.set(Calendar.MINUTE,59)
        calculatedEndTime.set(Calendar.SECOND,59)
        Long diff = calculatedEndTime.getTime().getTime() - displayStartDateAndTime.getTime()
        log.trace "Start time - ${displayStartDateAndTime} and end time - ${calculatedEndTime.getTime()} and diff in hours is ${TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)}}"
        TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)
    }

    /**
     * Calculates display start date
     * @param scheduledDate scheduled date and time
     * @param actionItemPostRecurringDetails actionItemPostRecurringDetails containing details required for recurrence
     * @return
     */
    Date resolveDisplayStartDate(Date scheduledDate, ActionItemPostRecurringDetails actionItemPostRecurringDetails) {
        if (actionItemPostRecurringDetails.postingDispStartDays == 0) {
            return removeTime(scheduledDate)
        } else {
            Integer daysToadd=actionItemPostRecurringDetails.postingDispStartDays
            return removeTime(addDays(scheduledDate,daysToadd ))
        }
    }

    /**
     * Calculates the display end date
     * @param scheduledDate scheduled date and time
     * @param actionItemPostRecurringDetails actionItemPostRecurringDetails containing details required for recurrence
     * @return
     */
    Date resolveDiplayEndDate(Date scheduledDate, ActionItemPostRecurringDetails actionItemPostRecurringDetails) {
        Boolean isOffSetEndDate = !actionItemPostRecurringDetails.postingDisplayEndDate && actionItemPostRecurringDetails.postingDispEndDays !=null
        log.trace "Resolving display End date - Posting End date -{$actionItemPostRecurringDetails.postingDisplayEndDate}, Posting End date offset {$actionItemPostRecurringDetails.postingDispEndDays},isOffset - {$isOffSetEndDate}"
        if (isOffSetEndDate) {
            if (actionItemPostRecurringDetails.postingDispEndDays == 0) {
                return removeTime(scheduledDate)
            } else {
                Integer daysToAdd = actionItemPostRecurringDetails.postingDispEndDays;
                return removeTime(addDays(scheduledDate,daysToAdd ))
            }
        } else {
            return actionItemPostRecurringDetails.postingDisplayEndDate
        }
    }

    /**
     * Calculates the scheduled date and time
     * @param actionItemPostRecurringDetails actionItemPostRecurringDetails containing details required for recurrence
     * @param iteration
     * @return
     */
    Date resolveScheduleDateTime(ActionItemPostRecurringDetails actionItemPostRecurringDetails, Integer iteration) {
        Date dateTime =  actionItemPostRecurringDetails.recurStartTime
        if (actionItemPostRecurringDetails.recurFrequencyType == AIPConstants.RECURR_FREQUENCY_TYPE_DAYS) {
            Integer daysToAdd = actionItemPostRecurringDetails.recurFrequency * iteration
            return addDays(dateTime, daysToAdd)
        } else {
            Integer hourToAdd = actionItemPostRecurringDetails.recurFrequency * iteration
            return addHours(dateTime, hourToAdd)
        }

    }


    /**
     * Calculates the display  date and time
     * @param actionItemPostRecurringDetails actionItemPostRecurringDetails containing details required for recurrence
     * @param actionItemPost
     * @param iteration
     * @return
     */
    Date resolvePostingDisplayDateTime(ActionItemPostRecurringDetails actionItemPostRecurringDetails,ActionItemPost actionItemPost,Integer iteration){
        Date dateTime =  actionItemPost.postingDisplayDateTime
        if (actionItemPostRecurringDetails.recurFrequencyType == AIPConstants.RECURR_FREQUENCY_TYPE_DAYS) {
            Integer daysToAdd = actionItemPostRecurringDetails.recurFrequency * iteration
            return addDays(dateTime, daysToAdd)
        } else {
            Integer hourToAdd = actionItemPostRecurringDetails.recurFrequency * iteration
            return addHours(dateTime, hourToAdd)
        }

    }

    /**
     * Adds hours to given date
     * @param date date object
     * @param hoursToAdd hours to add
     * @return
     */
    private Date addHours(Date date, Integer hoursToAdd) {
        Calendar calculatedDate = Calendar.getInstance()
        calculatedDate.setTime(date)
        calculatedDate.add(Calendar.HOUR_OF_DAY, hoursToAdd)
        return calculatedDate.getTime()
    }
    /**
     * Adds days to given date
     * @param date date object
     * @param daysToadd hours to add
     * @return
     */
    private Date addDays(Date date, Integer daysToadd) {
        Calendar calculatedStartDate = Calendar.getInstance()
        calculatedStartDate.setTime(date)
        calculatedStartDate.add(Calendar.DAY_OF_MONTH, daysToadd)
        calculatedStartDate.getTime()
    }
    /**
     * Removes time from the given date
     * @param date date object
     * @param daysToadd hours to add
     * @return
     */
    private Date removeTime(Date date) {
        Calendar calculatedStartDate = Calendar.getInstance()
        calculatedStartDate.setTime(date)
        calculatedStartDate.set(Calendar.HOUR_OF_DAY,0)
        calculatedStartDate.set(Calendar.MINUTE,0)
        calculatedStartDate.set(Calendar.SECOND,0)
        calculatedStartDate.getTime()
    }
}
