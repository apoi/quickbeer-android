/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.utils;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ix.Ix;
import polanski.option.Option;
import quickbeer.android.data.pojos.Style;

public class Styles implements SimpleListSource<Style> {

    private final SparseArray<Style> styles = new SparseArray<>(114);

    private Collection<Style> values;

    public Styles() {
        initStyles();
    }

    @Override
    public Style getItem(int id) {
        return styles.get(id);
    }

    @Override
    public Collection<Style> getList() {
        if (values == null) {
            values = new ArrayList<>(styles.size());

            for (int i = 0; i < styles.size(); i++) {
                values.add(styles.valueAt(i));
            }
        }

        return Collections.unmodifiableCollection(values);
    }

    @NonNull
    public Option<Style> getStyle(@NonNull String styleName) {
        return Ix.from(getList())
                .filter(value -> value.getName().equals(styleName))
                .map(Option::ofObj)
                .first(Option.none());
    }

    @SuppressWarnings({"MagicNumber", "OverlyLongMethod"})
    private void initStyles() {
        styles.put(2, new Style(2, "Altbier"));
        styles.put(3, new Style(3, "Pale Lager"));
        styles.put(4, new Style(4, "Pilsener"));
        styles.put(5, new Style(5, "Porter"));
        styles.put(6, new Style(6, "Stout"));
        styles.put(7, new Style(7, "German Hefeweizen"));
        styles.put(8, new Style(8, "Malt Liquor"));
        styles.put(9, new Style(9, "Dunkler Bock"));
        styles.put(10, new Style(10, "Cider"));
        styles.put(11, new Style(11, "Barley Wine"));
        styles.put(12, new Style(12, "Belgian Ale"));
        styles.put(13, new Style(13, "Belgian Strong Ale"));
        styles.put(14, new Style(14, "Lambic - Fruit"));
        styles.put(15, new Style(15, "Brown Ale"));
        styles.put(16, new Style(16, "English Pale Ale"));
        styles.put(17, new Style(17, "India Pale Ale (IPA)"));
        styles.put(18, new Style(18, "American Pale Ale"));
        styles.put(19, new Style(19, "Wheat Ale"));
        styles.put(20, new Style(20, "Bitter"));
        styles.put(21, new Style(21, "Scottish Ale"));
        styles.put(22, new Style(22, "Dry Stout"));
        styles.put(23, new Style(23, "Sweet Stout"));
        styles.put(24, new Style(24, "Imperial Stout"));
        styles.put(25, new Style(25, "Weizen Bock"));
        styles.put(26, new Style(26, "Doppelbock"));
        styles.put(27, new Style(27, "Eisbock"));
        styles.put(28, new Style(28, "Dunkel"));
        styles.put(29, new Style(29, "Schwarzbier"));
        styles.put(30, new Style(30, "Classic German Pilsener"));
        styles.put(31, new Style(31, "Bohemian Pilsener"));
        styles.put(35, new Style(35, "Cream Ale"));
        styles.put(36, new Style(36, "American Dark Lager"));
        styles.put(37, new Style(37, "Oktoberfest/Märzen"));
        styles.put(39, new Style(39, "Kölsch"));
        styles.put(40, new Style(40, "Fruit Beer"));
        styles.put(41, new Style(41, "Smoked"));
        styles.put(42, new Style(42, "California Common"));
        styles.put(43, new Style(43, "Vienna"));
        styles.put(44, new Style(44, "Mead"));
        styles.put(45, new Style(45, "Saison"));
        styles.put(48, new Style(48, "Belgian White (Witbier)"));
        styles.put(52, new Style(52, "Sour Ale/Wild Ale"));
        styles.put(53, new Style(53, "Amber Ale"));
        styles.put(54, new Style(54, "Golden Ale/Blond Ale"));
        styles.put(55, new Style(55, "Mild Ale"));
        styles.put(56, new Style(56, "English Strong Ale"));
        styles.put(57, new Style(57, "Spice/Herb/Vegetable"));
        styles.put(58, new Style(58, "Bière de Garde"));
        styles.put(59, new Style(59, "Traditional Ale"));
        styles.put(60, new Style(60, "Dortmunder/Helles"));
        styles.put(61, new Style(61, "Berliner Weisse"));
        styles.put(62, new Style(62, "Irish Ale"));
        styles.put(63, new Style(63, "Baltic Porter"));
        styles.put(64, new Style(64, "American Strong Ale"));
        styles.put(65, new Style(65, "Strong Pale Lager/Imperial Pils"));
        styles.put(71, new Style(71, "Abbey Dubbel"));
        styles.put(72, new Style(72, "Abbey Tripel"));
        styles.put(73, new Style(73, "Lambic - Gueuze"));
        styles.put(74, new Style(74, "Zwickel/Keller/Landbier"));
        styles.put(75, new Style(75, "Low Alcohol"));
        styles.put(76, new Style(76, "Old Ale"));
        styles.put(77, new Style(77, "Lambic - Faro"));
        styles.put(78, new Style(78, "Lambic - Unblended"));
        styles.put(79, new Style(79, "Foreign Stout"));
        styles.put(80, new Style(80, "Abt/Quadrupel"));
        styles.put(81, new Style(81, "Imperial/Double IPA"));
        styles.put(82, new Style(82, "German Kristallweizen"));
        styles.put(84, new Style(84, "Saké - Junmai"));
        styles.put(85, new Style(85, "Saké - Honjozo"));
        styles.put(86, new Style(86, "Saké - Ginjo"));
        styles.put(87, new Style(87, "Saké - Daiginjo"));
        styles.put(88, new Style(88, "Saké - Tokubetsu"));
        styles.put(89, new Style(89, "Saké - Futsu-shu"));
        styles.put(90, new Style(90, "Saké - Namasaké"));
        styles.put(91, new Style(91, "Saké - Genshu"));
        styles.put(92, new Style(92, "Saké - Koshu"));
        styles.put(93, new Style(93, "Saké - Nigori"));
        styles.put(94, new Style(94, "Saké - Infused"));
        styles.put(95, new Style(95, "Saké - Taru"));
        styles.put(100, new Style(100, "Dunkelweizen"));
        styles.put(101, new Style(101, "Premium Bitter/ESB"));
        styles.put(102, new Style(102, "Scotch Ale"));
        styles.put(103, new Style(103, "Premium Lager"));
        styles.put(105, new Style(105, "Heller Bock"));
        styles.put(106, new Style(106, "Specialty Grain"));
        styles.put(107, new Style(107, "Perry"));
        styles.put(112, new Style(112, "Ice Cider/Perry"));
        styles.put(113, new Style(113, "Imperial/Strong Porter"));
        styles.put(114, new Style(114, "Black IPA"));
    }
}
