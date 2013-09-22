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

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

public class ExtendedResultHandler extends DefaultExecuteResultHandler {

    private ExecuteWatchdog watchdog;
    private ExecuteResult exitMode = ExecuteResult.OK_COMPLETED;

    public ExtendedResultHandler(ExecuteWatchdog watchdog) {
        this.watchdog = watchdog;
    }

    public ExecuteResult getExitMode() {
        return exitMode;
    }

    @Override
    public void onProcessComplete(int exitValue) {
        super.onProcessComplete(exitValue);
        exitMode = ExecuteResult.OK_COMPLETED;
    }

    @Override
    public void onProcessFailed(ExecuteException e) {
        // TODO Auto-generated method stub
        super.onProcessFailed(e);
        if (watchdog != null && watchdog.killedProcess()) {
            exitMode = ExecuteResult.ERROR_TIMED_OUT;
        } else if (e.getCause() == null) {
            exitMode = ExecuteResult.ERROR_COMPLETED;
        } else {
            exitMode = ExecuteResult.ERROR_IN_EXECUTION;
        }
    }
}
