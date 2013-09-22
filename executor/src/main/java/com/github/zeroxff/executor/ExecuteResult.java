/*
 * Copyright 2013 Francesco Fioravanti
 * 
 * This file is part of ExtendedExecutor.
 * 
 * ExtendedExecutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * ExtendedExecutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ExtendedExecutor.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */


package com.github.zeroxff.executor;

public enum ExecuteResult {
    OK_COMPLETED,ERROR_COMPLETED, ERROR_TIMED_OUT, ERROR_IN_EXECUTION, EXCEPTION
}