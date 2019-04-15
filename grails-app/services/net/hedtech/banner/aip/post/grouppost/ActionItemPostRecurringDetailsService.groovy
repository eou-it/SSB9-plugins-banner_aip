/*******************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger

import java.util.concurrent.TimeUnit
/**
 * ActionItemPost Recurring Details Service is responsible for initiating and processing recurring posts.
 */

class ActionItemPostRecurringDetailsService extends ServiceBase {

    private static
    final LOGGER = Logger.getLogger("net.hedtech.banner.aip.post.grouppost.ActionItemPostRecurringDetailsService")

    /**
     * validates user intpus
     * @param map map of user inputs
     */
    void preCreateValidate(map) {
        LOGGER.trace "Request map -{$map}"

        if (!map) {
            throw new ApplicationException(ActionItemPostService, new BusinessLogicValidationException('preCreate.validation.insufficient.request', []))
        }
        if (map && map.recurFrequency <= 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.frequency.greater.zero', []))
        }
        if (map && !map.recurFrequencyType) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.recurFrequencyType.invalid', []))
        }
        if (map && map.postingDispStartDays && map.postingDispStartDays < 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.displayStartDateOffset.zero', []))
        }
        if (map && map.postingDispEndDays && !map.postingDisplayEndDate && map.postingDispEndDays < 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.postingDispEndDays.zero', []))
        }
        if (map && map.postingDispEndDays && !map.postingDisplayEndDate && map.postingDispEndDays < map.postingDispStartDays) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.postingDisplayEndDate.greater.postingDispStartDays', []))
        }
        if (map && !map.postingDispEndDays && map.postingDisplayEndDate && map.postingDisplayEndDate.compareTo(map.recurStartDate) <= 0) {
            throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('preCreate.validation.recurrence.postingDisplayEndDate.greater.than.recurStartDate', []))
        }
        if (map && !map.postingDispEndDays && map.postingDisplayEndDate && !(map.postingDisplayEndDate.compareTo(map.recurEndDate) > 0)) {
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
    Boolean validateDisplayEndDate(ActionItemPostRecurringDetails actionItemPostRecurringDetails){
        if(!actionItemPostRecurringDetails.postingDispEndDays && actionItemPostRecurringDetails.postingDisplayEndDate ){
            //when firm dates have been given
            Long numberOfObjects = getNumberOfJobs(actionItemPostRecurringDetails)
            Date postingDateOfLastJob = resolveScheduleDateTime(actionItemPostRecurringDetails,numberOfObjects-1)
            if(postingDateOfLastJob.compareTo(actionItemPostRecurringDetails.postingDisplayEndDate)<0){
                //if firm display end date chosen & calculated last posting job date < display end date
                //Display end date must be greater than or equal to date of last posting job.
                throw new ApplicationException(ActionItemPostRecurringDetailsService, new BusinessLogicValidationException('validation.DispEndDate.greater.than.or.equals.dateLastPosting', []))
            }
        }
    }

    /**
     * Calculates the number of that a recurring post creates
     * @param actionItemPostRecurringDetails metadata of recurring action item
     * @return
     */
    Long getNumberOfJobs(ActionItemPostRecurringDetails actionItemPostRecurringDetails) {
        Long totalNumber = actionItemPostRecurringDetails.recurFrequencyType == AIPConstants.RECURR_FREQUENCY_TYPE_DAYS ? getDaysBetweenRecurStartAndEndDate(actionItemPostRecurringDetails)
                : getHoursBetweenRecurStartAndEndDate(actionItemPostRecurringDetails)
        Long numberOfObjects = totalNumber / actionItemPostRecurringDetails.recurFrequency
        LOGGER.trace "Number of jobs for reccuring job - ${actionItemPostRecurringDetails.id} is ${numberOfObjects}"
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
    Long getHoursBetweenRecurStartAndEndDate(ActionItemPostRecurringDetails actionItemPostRecurringDetails) {
        Calendar startTime = Calendar.getInstance()
        startTime.setTime(actionItemPostRecurringDetails.recurStartTime)
        Calendar calculatedStartDate = Calendar.getInstance()
        calculatedStartDate.setTime(actionItemPostRecurringDetails.recurStartDate)
        calculatedStartDate.add(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY))
        calculatedStartDate.add(Calendar.MINUTE, startTime.get(Calendar.MINUTE))
        Long diff = actionItemPostRecurringDetails.recurEndDate.getTime() - calculatedStartDate.getTime().getTime()
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
            return scheduledDate
        } else {
            Integer daysToadd=actionItemPostRecurringDetails.postingDispStartDays
            return addDays(scheduledDate,daysToadd )
        }
    }

    /**
     * Calculates the display end date
     * @param scheduledDate scheduled date and time
     * @param actionItemPostRecurringDetails actionItemPostRecurringDetails containing details required for recurrence
     * @return
     */
    Date resolveDiplayEndDate(Date scheduledDate, ActionItemPostRecurringDetails actionItemPostRecurringDetails) {
        Boolean isOffSetEndDate = !actionItemPostRecurringDetails.postingDisplayEndDate && actionItemPostRecurringDetails.postingDispEndDays
        if (isOffSetEndDate) {
            if (actionItemPostRecurringDetails.postingDispEndDays == 0) {
                return scheduledDate
            } else {
                Integer daysToAdd = actionItemPostRecurringDetails.postingDispEndDays
                return addDays(scheduledDate,daysToAdd )
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
}
