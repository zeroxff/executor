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

import org.apache.commons.exec.ExecuteException;

public class ExtendedExecuteException extends ExecuteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1701407623604900074L;

	public ExtendedExecuteException(String message, int exitValue) {
		super(message, exitValue);
	}

	public ExtendedExecuteException(String message, int exitValue,
			Throwable cause) {
		super(message, exitValue, cause);
	}

}
