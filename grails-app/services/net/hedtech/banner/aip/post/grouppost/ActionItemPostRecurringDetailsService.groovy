/*******************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.sql.Sql
import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.exceptions.ActionItemExceptionFactory
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.exceptions.NotFoundException
import net.hedtech.banner.general.communication.population.*
import net.hedtech.banner.general.scheduler.SchedulerErrorContext
import net.hedtech.banner.general.scheduler.SchedulerJobContext
import net.hedtech.banner.general.scheduler.SchedulerJobReceipt
import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder

/**
 * ActionItemPost Recurring Details Service is responsible for initiating and processing recurring posts.
 */

class ActionItemPostRecurringDetailsService  extends ServiceBase {

    private static
    final LOGGER = Logger.getLogger( "net.hedtech.banner.aip.post.grouppost.ActionItemPostRecurringDetailsService" )



}
