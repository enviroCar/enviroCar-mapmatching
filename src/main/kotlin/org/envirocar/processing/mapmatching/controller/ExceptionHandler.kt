/**
 *
 * Copyright (C) 2013-2019 The enviroCar project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.envirocar.processing.mapmatching.controller

import org.envirocar.processing.mapmatching.exceptions.InvalidInputException
import org.envirocar.processing.mapmatching.exceptions.OutOfSuppertedAreaException
import org.envirocar.processing.mapmatching.models.ErrorType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * @author dewall
 */
@ControllerAdvice
@RestController
class ExceptionHandler : ResponseEntityExceptionHandler() {
    private val LOG: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(InvalidInputException::class)
    fun invalidInput(ex: InvalidInputException) = {
        LOG.error(ex.message, ex)
        ResponseEntity(ErrorType(HttpStatus.BAD_REQUEST.value(), ex.message, ex.toString()), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(OutOfSuppertedAreaException::class)
    fun outOfSupportedArea(ex: OutOfSuppertedAreaException) = {
        LOG.error(ex.message, ex)
        ResponseEntity(ErrorType(HttpStatus.BAD_REQUEST.value(), ex.message, ex.toString()), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun genericException(ex: Exception) = {
        LOG.error(ex.message, ex)
        ResponseEntity(ErrorType(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message, ex.toString()), HttpStatus.INTERNAL_SERVER_ERROR)
    }

}