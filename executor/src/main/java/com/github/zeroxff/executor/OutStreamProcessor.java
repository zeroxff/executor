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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.exec.LogOutputStream;

/**
 * @author francesco
 * 
 */
public class OutStreamProcessor extends LogOutputStream {
    private List<Pattern> patternList = null;
    private List<String> linee = new ArrayList<>();

    public OutStreamProcessor() {
        super();
    }

    public OutStreamProcessor(int level) {
        super(level);
    }

    public OutStreamProcessor(List<Pattern> patternList) {
        super();
        this.patternList = patternList;
    }

    public OutStreamProcessor(List<Pattern> patternList, int level) {
        super(level);
        this.patternList = patternList;
    }

    @Override
    protected void processLine(String linea, int livello) {
        if (patternList != null) {
            for (Pattern tmpPattern : patternList) {
                if (tmpPattern.matcher(linea).matches()) {
                    linee.add(linea);
                    break;
                }
            }
        } else {
            linee.add(linea);
        }
    }

    public List<String> getLines() {
        return linee;
    }

}
