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
package quickbeer.android.next.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Countries {

    private final Map<Integer, String> countries = new HashMap<>();

    public Countries() {
        initCountries();
    }

    public String getCountry(int countryCode) {
        return countries.get(countryCode);
    }

    public Collection<String> getCountries() {
        return countries.values();
    }

    private void initCountries() {
        countries.put(1, "Afghanistan");
        countries.put(2, "Albania");
        countries.put(4, "Algeria");
        countries.put(5, "American Samoa");
        countries.put(6, "Andorra");
        countries.put(7, "Angola");
        countries.put(8, "Anguilla");
        countries.put(9, "Antigua & Barbuda");
        countries.put(10, "Argentina");
        countries.put(11, "Armenia");
        countries.put(12, "Aruba");
        countries.put(13, "Ascension Island");
        countries.put(14, "Australia");
        countries.put(15, "Austria");
        countries.put(16, "Azerbaijan");
        countries.put(17, "Bahamas");
        countries.put(18, "Bahrain");
        countries.put(19, "Bangladesh");
        countries.put(20, "Barbados");
        countries.put(22, "Belarus");
        countries.put(23, "Belgium");
        countries.put(24, "Belize");
        countries.put(25, "Benin");
        countries.put(26, "Bermuda");
        countries.put(27, "Bhutan");
        countries.put(28, "Bolivia");
        countries.put(29, "Bosnia");
        countries.put(30, "Botswana");
        countries.put(31, "Brazil");
        countries.put(32, "British Virgin Islands");
        countries.put(33, "Brunei");
        countries.put(34, "Bulgaria");
        countries.put(35, "Burkina Faso");
        countries.put(36, "Burundi");
        countries.put(37, "Cambodia");
        countries.put(38, "Cameroon");
        countries.put(39, "Canada");
        countries.put(40, "Cape Verde Islands");
        countries.put(41, "Cayman Islands");
        countries.put(42, "Central African Republic");
        countries.put(43, "Chad");
        countries.put(44, "Chile");
        countries.put(45, "China");
        countries.put(46, "Christmas Island");
        countries.put(47, "Cocos-Keeling Islands");
        countries.put(48, "Colombia");
        countries.put(49, "Comoros");
        countries.put(50, "Congo");
        countries.put(51, "Cook Islands");
        countries.put(52, "Costa Rica");
        countries.put(53, "Croatia");
        countries.put(54, "Cuba");
        countries.put(55, "Cyprus");
        countries.put(56, "Czech Republic");
        countries.put(57, "Dem Rep of Congo");
        countries.put(58, "Denmark");
        countries.put(59, "Diego Garcia");
        countries.put(60, "Djibouti");
        countries.put(61, "Dominica");
        countries.put(62, "Dominican Republic");
        countries.put(63, "Ecuador");
        countries.put(64, "Egypt");
        countries.put(65, "El Salvador");
        countries.put(66, "Equatorial Guinea");
        countries.put(67, "Estonia");
        countries.put(68, "Ethiopia");
        countries.put(69, "Falkland Islands");
        countries.put(70, "Fiji Islands");
        countries.put(71, "Finland");
        countries.put(72, "France");
        countries.put(74, "French Guiana");
        countries.put(75, "French Polynesia");
        countries.put(76, "Gabon");
        countries.put(77, "Gambia");
        countries.put(78, "Georgia");
        countries.put(79, "Germany");
        countries.put(80, "Ghana");
        countries.put(81, "Gibraltar");
        countries.put(82, "Greece");
        countries.put(83, "Greenland");
        countries.put(84, "Grenada");
        countries.put(85, "Guadeloupe");
        countries.put(86, "Guam");
        countries.put(88, "Guatemala");
        countries.put(89, "Guinea");
        countries.put(90, "Guyana");
        countries.put(91, "Haiti");
        countries.put(92, "Honduras");
        countries.put(93, "Hong Kong");
        countries.put(94, "Hungary");
        countries.put(95, "Iceland");
        countries.put(96, "India");
        countries.put(97, "Indonesia");
        countries.put(98, "Iran");
        countries.put(99, "Iraq");
        countries.put(100, "Ireland");
        countries.put(101, "Israel");
        countries.put(102, "Italy");
        countries.put(103, "Ivory Coast");
        countries.put(104, "Jamaica");
        countries.put(105, "Japan");
        countries.put(106, "Jordan");
        countries.put(107, "Kazakhstan");
        countries.put(108, "Kenya");
        countries.put(109, "Kiribati Republic");
        countries.put(110, "North Korea");
        countries.put(111, "South Korea");
        countries.put(112, "Kuwait");
        countries.put(113, "Kyrgyz Republic");
        countries.put(114, "Laos");
        countries.put(115, "Latvia");
        countries.put(116, "Lebanon");
        countries.put(117, "Lesotho");
        countries.put(118, "Liberia");
        countries.put(119, "Libya");
        countries.put(120, "Liechtenstein");
        countries.put(121, "Lithuania");
        countries.put(122, "Luxembourg");
        countries.put(123, "Macau");
        countries.put(124, "Madagascar");
        countries.put(125, "Malawi");
        countries.put(126, "Malaysia");
        countries.put(127, "Maldives");
        countries.put(128, "Mali");
        countries.put(129, "Malta");
        countries.put(130, "Marshall Islands");
        countries.put(131, "Martinique");
        countries.put(132, "Mayotte Island");
        countries.put(133, "Mexico");
        countries.put(134, "Moldova");
        countries.put(135, "Monaco");
        countries.put(136, "Mongolia");
        countries.put(137, "Monserrat");
        countries.put(138, "Morocco");
        countries.put(139, "Mozambique");
        countries.put(140, "Myanmar");
        countries.put(141, "Namibia");
        countries.put(142, "Nauru");
        countries.put(143, "Nepal");
        countries.put(144, "Netherlands");
        countries.put(145, "Netherlands Antilles");
        countries.put(147, "New Caledonia");
        countries.put(148, "New Zealand");
        countries.put(149, "Nicaragua");
        countries.put(150, "Niger");
        countries.put(151, "Nigeria");
        countries.put(152, "Niue");
        countries.put(153, "Norfolk Island");
        countries.put(154, "Norway");
        countries.put(155, "Oman");
        countries.put(156, "Pakistan");
        countries.put(157, "Palau");
        countries.put(158, "Panama");
        countries.put(159, "Papua New Guinea");
        countries.put(160, "Paraguay");
        countries.put(161, "Peru");
        countries.put(162, "Philippines");
        countries.put(163, "Poland");
        countries.put(164, "Portugal");
        countries.put(165, "Puerto Rico");
        countries.put(166, "Qatar");
        countries.put(167, "Romania");
        countries.put(169, "Russia");
        countries.put(170, "Rwanda");
        countries.put(171, "St Lucia");
        countries.put(172, "Saipan Island");
        countries.put(173, "San Marino");
        countries.put(174, "Saudi Arabia");
        countries.put(175, "Senegal Republic");
        countries.put(176, "Sierra Leone");
        countries.put(177, "Singapore");
        countries.put(178, "Slovak Republic");
        countries.put(179, "Slovenia");
        countries.put(180, "Solomon Islands");
        countries.put(181, "Somalia");
        countries.put(182, "South Africa");
        countries.put(183, "Spain");
        countries.put(184, "Sri Lanka");
        countries.put(185, "St Helena");
        countries.put(186, "St Kitts");
        countries.put(187, "Sudan");
        countries.put(188, "Suriname");
        countries.put(189, "Swaziland");
        countries.put(190, "Sweden");
        countries.put(191, "Switzerland");
        countries.put(192, "Syria");
        countries.put(193, "Taiwan");
        countries.put(194, "Tajikistan");
        countries.put(195, "Tanzania");
        countries.put(196, "Thailand");
        countries.put(197, "Tinian Island");
        countries.put(198, "Togo");
        countries.put(199, "Tokelau");
        countries.put(200, "Tonga");
        countries.put(201, "Trinidad & Tobago");
        countries.put(202, "Tunisia");
        countries.put(203, "Turkey");
        countries.put(204, "Turkmenistan");
        countries.put(205, "Turks and Caicos Islands");
        countries.put(206, "Tuvalu");
        countries.put(207, "Uganda");
        countries.put(208, "Ukraine");
        countries.put(209, "United Arab Emirates");
        countries.put(211, "United States Virgin Islands");
        countries.put(212, "Uruguay");
        countries.put(213, "United States");
        countries.put(214, "Uzbekistan");
        countries.put(215, "Vanuatu");
        countries.put(216, "Vatican City");
        countries.put(217, "Venezuela");
        countries.put(218, "Vietnam");
        countries.put(219, "Samoa");
        countries.put(221, "Serbia");
        countries.put(222, "Zambia");
        countries.put(223, "Zimbabwe");
        countries.put(224, "Isle of Man");
        countries.put(225, "Guernsey");
        countries.put(226, "Jersey");
        countries.put(227, "St Vincent & The Grenadines");
        countries.put(229, "Macedonia");
        countries.put(230, "Mauritius");
        countries.put(233, "Ceuta");
        countries.put(234, "Montenegro");
        countries.put(235, "Palestine");
        countries.put(236, "East Timor");
        countries.put(237, "Tibet");
        countries.put(238, "Northern Ireland");
        countries.put(239, "Wales");
        countries.put(240, "England");
        countries.put(241, "Scotland");
        countries.put(242, "Kosovo");
        countries.put(243, "Eritrea");
        countries.put(244, "Faroe Islands");
        countries.put(245, "Seychelles");
        countries.put(246, "Réunion");
        countries.put(247, "Micronesia");
        countries.put(248, "Yemen");
        countries.put(253, "Guinea-Bissau");
        countries.put(254, "São Tomé & Principe");
        countries.put(255, "Northern Marianas");
    }
}
