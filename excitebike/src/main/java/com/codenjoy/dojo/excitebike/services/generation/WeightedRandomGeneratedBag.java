package com.codenjoy.dojo.excitebike.services.generation;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.Dice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Bobylev 7/18/2019
 */
public class WeightedRandomGeneratedBag {

    private List<Entry> entries = new ArrayList<>();
    private int accumulatedWeight;

    public void addEntry(GenerationOption generationOption, int weight) {
        this.accumulatedWeight += weight;
        Entry entry = new Entry(accumulatedWeight, generationOption);
        entries.add(entry);
    }

    public GenerationOption getRandom(Dice dice) {
        int r = dice.next(accumulatedWeight + 1);

        return entries.stream()
                .filter(entry -> entry.weight >= r)
                .findFirst()
                .map(entry -> entry.generationOption)
                .orElse(null);
    }

    private class Entry {
        final int weight;
        final GenerationOption generationOption;

        public Entry(int weight, GenerationOption generationOption) {
            this.weight = weight;
            this.generationOption = generationOption;
        }
    }
}
