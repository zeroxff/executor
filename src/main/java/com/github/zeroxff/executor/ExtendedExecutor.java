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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

public class ExtendedExecutor {

	/*
	 * In
	 */
	private String[] commandLine = null;
	private Map<String, Object> substitutionMap = null;
	private int[] okExitValues = null;
	private List<Pattern> outputFilter = null;
	private List<Pattern> errorFilter = null;
	private long maxExecutiontime = ExecuteWatchdog.INFINITE_TIMEOUT;
	private File workingDirecory = null;
	private boolean mergeOutStreams = false;
	private boolean quoteCommandlineArgs = true;

	/*
	 * Out
	 */
	private ExecuteResult exitMode = ExecuteResult.OK_COMPLETED;
	private int returnCode = Executor.INVALID_EXITVALUE;
	private List<String> outputLines;
	private List<String> errorLines;
	private Throwable exception;
	private String message;

	/*
	 * Misc
	 */

	// bean interface, getters for output values

	public ExecuteResult getExitMode() {
		return exitMode;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public List<String> getOutputLines() {
		return outputLines;
	}

	public List<String> getErrorLines() {
		return errorLines;
	}

	public Throwable getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}

	// fluent interface
	public ExtendedExecutor withCommandLine(String[] commandLine) {
		this.commandLine = commandLine;
		return this;
	}

	public ExtendedExecutor withSubstitutionMap(
			Map<String, Object> substitutionMap) {
		this.substitutionMap = substitutionMap;
		return this;
	}

	public ExtendedExecutor withOkExitValues(int[] okExitValues) {
		this.okExitValues = okExitValues;
		return this;
	}

	public ExtendedExecutor withOutputFilter(List<Pattern> outputFilter) {
		this.outputFilter = outputFilter;
		return this;
	}

	public ExtendedExecutor withErrorFilter(List<Pattern> errorFilter) {
		this.errorFilter = errorFilter;
		return this;
	}

	public ExtendedExecutor withMaxExecutiontime(long maxExecutiontime) {
		this.maxExecutiontime = maxExecutiontime;
		return this;
	}

	public ExtendedExecutor withInfiniteTimeout() {
		this.maxExecutiontime = ExecuteWatchdog.INFINITE_TIMEOUT;
		return this;
	}

	public ExtendedExecutor withWorkingDirecory(File workingDirecory) {
		this.workingDirecory = workingDirecory;
		return this;
	}

	public ExtendedExecutor withMergedOutputStreams() {
		this.mergeOutStreams = true;
		return this;
	}

	public ExtendedExecutor withSeparatedOutputStreams() {
		this.mergeOutStreams = false;
		return this;
	}

	public ExtendedExecutor withArgumentQuoting() {
		this.quoteCommandlineArgs = true;
		return this;
	}

	public ExtendedExecutor withoutArgumentQuoting() {
		this.quoteCommandlineArgs = false;
		return this;
	}

	public ExtendedExecutor clear() {
		this.clearIn();
		this.clearOut();
		return this;
	}

	public ExecuteResult execute(String[] commandLine)
			throws ExtendedExecuteException {
		this.commandLine = commandLine;
		return this.execute();
	}

	public ExecuteResult execute() throws ExtendedExecuteException {
		this.clearOut();
		if (this.commandLine == null) {
			throw new ExtendedExecuteException("CommandLine cannot be null",
					Executor.INVALID_EXITVALUE);
		}
		if (this.commandLine.length == 0) {
			throw new ExtendedExecuteException("CommandLine cannot be empty",
					Executor.INVALID_EXITVALUE);
		}
		if (this.maxExecutiontime != ExecuteWatchdog.INFINITE_TIMEOUT
				&& this.maxExecutiontime < 1) {
			throw new ExtendedExecuteException(
					"Max execution time must not be less than 1",
					Executor.INVALID_EXITVALUE);
		}

		try {
			// load the command line as an array of strings
			CommandLine cmdLine = new CommandLine(this.commandLine[0]);
			for (int counter = 1; counter < commandLine.length; counter++) {
				cmdLine.addArgument(this.commandLine[counter], quoteCommandlineArgs);
			}

			// load the substitution map, if defined
			if (this.substitutionMap != null) {
				cmdLine.setSubstitutionMap(this.substitutionMap);
			}

			// load the watchdog timer, it can be set to infinite time
			ExecuteWatchdog watchdog = new ExecuteWatchdog(
					this.maxExecutiontime);
			ExtendedResultHandler resultHandler = new ExtendedResultHandler(
					watchdog);

			// inizialize outputstream processors.
			OutStreamProcessor outLinee = null;
			OutStreamProcessor errLinee = null;
			PumpStreamHandler streamHandler = null;
			if (outputFilter != null && outputFilter.size() > 0) {
				outLinee = new OutStreamProcessor(outputFilter);
			} else {
				outLinee = new OutStreamProcessor();
			}
			if (mergeOutStreams) {
				// Using Std out for the output/error stream
				streamHandler = new PumpStreamHandler(outLinee);
			} else {
				if (errorFilter != null && errorFilter.size() > 0) {
					errLinee = new OutStreamProcessor(errorFilter);
				} else {
					errLinee = new OutStreamProcessor();
				}
				// Using Std out for the output/error stream
				streamHandler = new PumpStreamHandler(outLinee, errLinee);
			}
			DefaultExecutor executor = new DefaultExecutor();
			// set the working directory...
			// if the working directory doesn't exists, it can crash the
			// executor.
			if (workingDirecory != null) {
				executor.setWorkingDirectory(workingDirecory);
			}

			// set the accepted exit values for the command line
			// default is '0'.
			if (okExitValues != null && okExitValues.length > 0) {
				executor.setExitValues(okExitValues);
			}

			executor.setWatchdog(watchdog);
			executor.setStreamHandler(streamHandler);

			try {
				executor.execute(cmdLine, resultHandler);
				resultHandler.waitFor();
				returnCode = resultHandler.getExitValue();
				exitMode = resultHandler.getExitMode();
				switch (exitMode) {
				case ERROR_IN_EXECUTION:
					this.message = resultHandler.getException().getMessage();
					break;
				default:
					break;
				}
			} catch (ExecuteException e) {
				exitMode = ExecuteResult.EXCEPTION;
				exception = e;
				this.message = e.getMessage();
			} catch (IOException e) {
				exitMode = ExecuteResult.EXCEPTION;
				exception = e;
				this.message = e.getMessage();
			} catch (InterruptedException e) {
				exitMode = ExecuteResult.EXCEPTION;
				exception = e;
				this.message = e.getMessage();
			}

			//

			if (outLinee != null) {
				outputLines = outLinee.getLines();
			}
			if (errLinee != null) {
				errorLines = errLinee.getLines();
			}

			this.closeStreams(outLinee, errLinee);
		} catch (Exception e) {
			throw new ExtendedExecuteException(e.getMessage(),
					Executor.INVALID_EXITVALUE, e);
		}

		return exitMode;
	}

	private void closeStreams(OutputStream streamOut, OutputStream streamErr)
			throws IOException {
		IOException caught = null;

		if (streamOut != null) {
			try {
				streamOut.close();
			} catch (IOException e) {
				caught = e;
			}
		}

		if (streamErr != null) {
			try {
				streamErr.close();
			} catch (IOException e) {
				caught = e;
			}
		}

		if (caught != null) {
			throw caught;
		}
	}

	private void clearIn() {
		commandLine = null;
		substitutionMap = null;
		okExitValues = null;
		outputFilter = null;
		errorFilter = null;
		maxExecutiontime = ExecuteWatchdog.INFINITE_TIMEOUT;
		workingDirecory = null;
		mergeOutStreams = false;
		quoteCommandlineArgs = true;
	}

	private void clearOut() {
		exitMode = ExecuteResult.OK_COMPLETED;
		returnCode = Executor.INVALID_EXITVALUE;
		outputLines = null;
		errorLines = null;
		exception = null;
		message = null;
	}

}
