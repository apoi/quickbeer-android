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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import quickbeer.android.data.pojos.Country;
import quickbeer.android.data.pojos.SimpleItem;

public class Countries implements SimpleListSource {

    private final Map<Integer, SimpleItem> countries = new HashMap<>();

    public Countries() {
        initCountries();
    }

    @Override
    public SimpleItem getItem(int id) {
        return countries.get(id);
    }

    @Override
    public Collection<SimpleItem> getList() {
        return countries.values();
    }

    private void initCountries() {
        countries.put(1, new Country(1, "Afghanistan", "AF"));
        countries.put(2, new Country(2, "Albania", "AL"));
        countries.put(4, new Country(4, "Algeria", "DZ"));
        countries.put(5, new Country(5, "American Samoa", "AS"));
        countries.put(6, new Country(6, "Andorra", "AD"));
        countries.put(7, new Country(7, "Angola", "AO"));
        countries.put(8, new Country(8, "Anguilla", "AI"));
        countries.put(9, new Country(9, "Antigua & Barbuda", "AG"));
        countries.put(10, new Country(10, "Argentina", "AR"));
        countries.put(11, new Country(11, "Armenia", "AM"));
        countries.put(12, new Country(12, "Aruba", "AW"));
        countries.put(13, new Country(13, "Ascension Island", "AC"));
        countries.put(14, new Country(14, "Australia", "AU"));
        countries.put(15, new Country(15, "Austria", "AT"));
        countries.put(16, new Country(16, "Azerbaijan", "AZ"));
        countries.put(17, new Country(17, "Bahamas", "BS"));
        countries.put(18, new Country(18, "Bahrain", "BH"));
        countries.put(19, new Country(19, "Bangladesh", "BD"));
        countries.put(20, new Country(20, "Barbados", "BB"));
        countries.put(22, new Country(22, "Belarus", "BY"));
        countries.put(23, new Country(23, "Belgium", "BE"));
        countries.put(24, new Country(24, "Belize", "BZ"));
        countries.put(25, new Country(25, "Benin", "BJ"));
        countries.put(26, new Country(26, "Bermuda", "BM"));
        countries.put(27, new Country(27, "Bhutan", "BT"));
        countries.put(28, new Country(28, "Bolivia", "BO"));
        countries.put(29, new Country(29, "Bosnia", "BA"));
        countries.put(30, new Country(30, "Botswana", "BW"));
        countries.put(31, new Country(31, "Brazil", "BR"));
        countries.put(32, new Country(32, "British Virgin Islands", "VG"));
        countries.put(33, new Country(33, "Brunei", "BN"));
        countries.put(34, new Country(34, "Bulgaria", "BG"));
        countries.put(35, new Country(35, "Burkina Faso", "BF"));
        countries.put(36, new Country(36, "Burundi", "BI"));
        countries.put(37, new Country(37, "Cambodia", "KH"));
        countries.put(38, new Country(38, "Cameroon", "CM"));
        countries.put(39, new Country(39, "Canada", "CA"));
        countries.put(40, new Country(40, "Cape Verde Islands", "CV"));
        countries.put(41, new Country(41, "Cayman Islands", "KY"));
        countries.put(42, new Country(42, "Central African Republic", "CF"));
        countries.put(43, new Country(43, "Chad", "TD"));
        countries.put(44, new Country(44, "Chile", "CL"));
        countries.put(45, new Country(45, "China", "CN"));
        countries.put(46, new Country(46, "Christmas Island", "CX"));
        countries.put(47, new Country(47, "Cocos-Keeling Islands", "CC"));
        countries.put(48, new Country(48, "Colombia", "CO"));
        countries.put(49, new Country(49, "Comoros", "KM"));
        countries.put(50, new Country(50, "Congo", "CG"));
        countries.put(51, new Country(51, "Cook Islands", "CK"));
        countries.put(52, new Country(52, "Costa Rica", "CR"));
        countries.put(53, new Country(53, "Croatia", "HR"));
        countries.put(54, new Country(54, "Cuba", "CU"));
        countries.put(55, new Country(55, "Cyprus", "CY"));
        countries.put(56, new Country(56, "Czech Republic", "CZ"));
        countries.put(57, new Country(57, "Dem Rep of Congo", "CD"));
        countries.put(58, new Country(58, "Denmark", "DK"));
        countries.put(59, new Country(59, "Diego Garcia", "IO"));
        countries.put(60, new Country(60, "Djibouti", "DJ"));
        countries.put(61, new Country(61, "Dominica", "DM"));
        countries.put(62, new Country(62, "Dominican Republic", "DO"));
        countries.put(63, new Country(63, "Ecuador", "EC"));
        countries.put(64, new Country(64, "Egypt", "EG"));
        countries.put(65, new Country(65, "El Salvador", "SV"));
        countries.put(66, new Country(66, "Equatorial Guinea", "GQ"));
        countries.put(67, new Country(67, "Estonia", "EE"));
        countries.put(68, new Country(68, "Ethiopia", "ET"));
        countries.put(69, new Country(69, "Falkland Islands", "FK"));
        countries.put(70, new Country(70, "Fiji Islands", "FJ"));
        countries.put(71, new Country(71, "Finland", "FI"));
        countries.put(72, new Country(72, "France", "FR"));
        countries.put(74, new Country(74, "French Guiana", "GF"));
        countries.put(75, new Country(75, "French Polynesia", "PF"));
        countries.put(76, new Country(76, "Gabon", "GA"));
        countries.put(77, new Country(77, "Gambia", "GM"));
        countries.put(78, new Country(78, "Georgia", "GE"));
        countries.put(79, new Country(79, "Germany", "DE"));
        countries.put(80, new Country(80, "Ghana", "GH"));
        countries.put(81, new Country(81, "Gibraltar", "GI"));
        countries.put(82, new Country(82, "Greece", "GR"));
        countries.put(83, new Country(83, "Greenland", "GL"));
        countries.put(84, new Country(84, "Grenada", "GD"));
        countries.put(85, new Country(85, "Guadeloupe", "GP"));
        countries.put(86, new Country(86, "Guam", "GU"));
        countries.put(88, new Country(88, "Guatemala", "GT"));
        countries.put(89, new Country(89, "Guinea", "GN"));
        countries.put(90, new Country(90, "Guyana", "GY"));
        countries.put(91, new Country(91, "Haiti", "HT"));
        countries.put(92, new Country(92, "Honduras", "HN"));
        countries.put(93, new Country(93, "Hong Kong", "HK"));
        countries.put(94, new Country(94, "Hungary", "HU"));
        countries.put(95, new Country(95, "Iceland", "IS"));
        countries.put(96, new Country(96, "India", "IN"));
        countries.put(97, new Country(97, "Indonesia", "ID"));
        countries.put(98, new Country(98, "Iran", "IR"));
        countries.put(99, new Country(99, "Iraq", "IQ"));
        countries.put(100, new Country(100, "Ireland", "IE"));
        countries.put(101, new Country(101, "Israel", "IL"));
        countries.put(102, new Country(102, "Italy", "IT"));
        countries.put(103, new Country(103, "Ivory Coast", "CI"));
        countries.put(104, new Country(104, "Jamaica", "JM"));
        countries.put(105, new Country(105, "Japan", "JP"));
        countries.put(106, new Country(106, "Jordan", "JO"));
        countries.put(107, new Country(107, "Kazakhstan", "KZ"));
        countries.put(108, new Country(108, "Kenya", "KE"));
        countries.put(109, new Country(109, "Kiribati Republic", "KI"));
        countries.put(110, new Country(110, "North Korea", "KP"));
        countries.put(111, new Country(111, "South Korea", "KR"));
        countries.put(112, new Country(112, "Kuwait", "KW"));
        countries.put(113, new Country(113, "Kyrgyz Republic", "KG"));
        countries.put(114, new Country(114, "Laos", "LA"));
        countries.put(115, new Country(115, "Latvia", "LV"));
        countries.put(116, new Country(116, "Lebanon", "LB"));
        countries.put(117, new Country(117, "Lesotho", "LS"));
        countries.put(118, new Country(118, "Liberia", "LR"));
        countries.put(119, new Country(119, "Libya", "LY"));
        countries.put(120, new Country(120, "Liechtenstein", "LI"));
        countries.put(121, new Country(121, "Lithuania", "LT"));
        countries.put(122, new Country(122, "Luxembourg", "LU"));
        countries.put(123, new Country(123, "Macau", "MO"));
        countries.put(124, new Country(124, "Madagascar", "MG"));
        countries.put(125, new Country(125, "Malawi", "MW"));
        countries.put(126, new Country(126, "Malaysia", "MY"));
        countries.put(127, new Country(127, "Maldives", "MV"));
        countries.put(128, new Country(128, "Mali", "ML"));
        countries.put(129, new Country(129, "Malta", "MT"));
        countries.put(130, new Country(130, "Marshall Islands", "MH"));
        countries.put(131, new Country(131, "Martinique", "MQ"));
        countries.put(132, new Country(132, "Mayotte Island", "YT"));
        countries.put(133, new Country(133, "Mexico", "MX"));
        countries.put(134, new Country(134, "Moldova", "MD"));
        countries.put(135, new Country(135, "Monaco", "MC"));
        countries.put(136, new Country(136, "Mongolia", "MN"));
        countries.put(137, new Country(137, "Monserrat", "MS"));
        countries.put(138, new Country(138, "Morocco", "MA"));
        countries.put(139, new Country(139, "Mozambique", "MZ"));
        countries.put(140, new Country(140, "Myanmar", "MM"));
        countries.put(141, new Country(141, "Namibia", "NA"));
        countries.put(142, new Country(142, "Nauru", "NR"));
        countries.put(143, new Country(143, "Nepal", "NP"));
        countries.put(144, new Country(144, "Netherlands", "NL"));
        countries.put(145, new Country(145, "Netherlands Antilles", "AN"));
        countries.put(147, new Country(147, "New Caledonia", "NC"));
        countries.put(148, new Country(148, "New Zealand", "NZ"));
        countries.put(149, new Country(149, "Nicaragua", "NI"));
        countries.put(150, new Country(150, "Niger", "NE"));
        countries.put(151, new Country(151, "Nigeria", "NG"));
        countries.put(152, new Country(152, "Niue", "NU"));
        countries.put(153, new Country(153, "Norfolk Island", "NF"));
        countries.put(154, new Country(154, "Norway", "NO"));
        countries.put(155, new Country(155, "Oman", "OM"));
        countries.put(156, new Country(156, "Pakistan", "PK"));
        countries.put(157, new Country(157, "Palau", "PW"));
        countries.put(158, new Country(158, "Panama", "PA"));
        countries.put(159, new Country(159, "Papua New Guinea", "PG"));
        countries.put(160, new Country(160, "Paraguay", "PY"));
        countries.put(161, new Country(161, "Peru", "PE"));
        countries.put(162, new Country(162, "Philippines", "PH"));
        countries.put(163, new Country(163, "Poland", "PL"));
        countries.put(164, new Country(164, "Portugal", "PT"));
        countries.put(165, new Country(165, "Puerto Rico", "PR"));
        countries.put(166, new Country(166, "Qatar", "QA"));
        countries.put(167, new Country(167, "Romania", "RO"));
        countries.put(169, new Country(169, "Russia", "RU"));
        countries.put(170, new Country(170, "Rwanda", "RW"));
        countries.put(171, new Country(171, "St Lucia", "LC"));
        countries.put(172, new Country(172, "Saipan Island", "MP"));
        countries.put(173, new Country(173, "San Marino", "SM"));
        countries.put(174, new Country(174, "Saudi Arabia", "SA"));
        countries.put(175, new Country(175, "Senegal Republic", "SN"));
        countries.put(176, new Country(176, "Sierra Leone", "SL"));
        countries.put(177, new Country(177, "Singapore", "SG"));
        countries.put(178, new Country(178, "Slovak Republic", "SK"));
        countries.put(179, new Country(179, "Slovenia", "SI"));
        countries.put(180, new Country(180, "Solomon Islands", "SB"));
        countries.put(181, new Country(181, "Somalia", "SO"));
        countries.put(182, new Country(182, "South Africa", "ZA"));
        countries.put(183, new Country(183, "Spain", "ES"));
        countries.put(184, new Country(184, "Sri Lanka", "LK"));
        countries.put(185, new Country(185, "St Helena", "SH"));
        countries.put(186, new Country(186, "St Kitts", "KN"));
        countries.put(187, new Country(187, "Sudan", "SD"));
        countries.put(188, new Country(188, "Suriname", "SR"));
        countries.put(189, new Country(189, "Swaziland", "SZ"));
        countries.put(190, new Country(190, "Sweden", "SE"));
        countries.put(191, new Country(191, "Switzerland", "CH"));
        countries.put(192, new Country(192, "Syria", "SY"));
        countries.put(193, new Country(193, "Taiwan", "TW"));
        countries.put(194, new Country(194, "Tajikistan", "TJ"));
        countries.put(195, new Country(195, "Tanzania", "TZ"));
        countries.put(196, new Country(196, "Thailand", "TH"));
        countries.put(197, new Country(197, "Tinian Island", "MP"));
        countries.put(198, new Country(198, "Togo", "TG"));
        countries.put(199, new Country(199, "Tokelau", "TK"));
        countries.put(200, new Country(200, "Tonga", "TO"));
        countries.put(201, new Country(201, "Trinidad & Tobago", "TT"));
        countries.put(202, new Country(202, "Tunisia", "TN"));
        countries.put(203, new Country(203, "Turkey", "TR"));
        countries.put(204, new Country(204, "Turkmenistan", "TM"));
        countries.put(205, new Country(205, "Turks and Caicos Islands", "TC"));
        countries.put(206, new Country(206, "Tuvalu", "TV"));
        countries.put(207, new Country(207, "Uganda", "UG"));
        countries.put(208, new Country(208, "Ukraine", "UA"));
        countries.put(209, new Country(209, "United Arab Emirates", "AE"));
        countries.put(211, new Country(211, "United States Virgin Islands", "VI"));
        countries.put(212, new Country(212, "Uruguay", "UY"));
        countries.put(213, new Country(213, "United States", "US"));
        countries.put(214, new Country(214, "Uzbekistan", "UZ"));
        countries.put(215, new Country(215, "Vanuatu", "VU"));
        countries.put(216, new Country(216, "Vatican City", "VA"));
        countries.put(217, new Country(217, "Venezuela", "VE"));
        countries.put(218, new Country(218, "Vietnam", "VN"));
        countries.put(219, new Country(219, "Samoa", "WS"));
        countries.put(221, new Country(221, "Serbia", "RS"));
        countries.put(222, new Country(222, "Zambia", "ZM"));
        countries.put(223, new Country(223, "Zimbabwe", "ZW"));
        countries.put(224, new Country(224, "Isle of Man", "IM"));
        countries.put(225, new Country(225, "Guernsey", "GG"));
        countries.put(226, new Country(226, "Jersey", "JE"));
        countries.put(227, new Country(227, "St Vincent & The Grenadines", "VC"));
        countries.put(229, new Country(229, "Macedonia", "MK"));
        countries.put(230, new Country(230, "Mauritius", "MU"));
        countries.put(233, new Country(233, "Ceuta", "ES"));
        countries.put(234, new Country(234, "Montenegro", "ME"));
        countries.put(235, new Country(235, "Palestine", "PS"));
        countries.put(236, new Country(236, "East Timor", "TL"));
        countries.put(237, new Country(237, "Tibet", "CN"));
        countries.put(238, new Country(238, "Northern Ireland", "UK"));
        countries.put(239, new Country(239, "Wales", "UK"));
        countries.put(240, new Country(240, "England", "UK"));
        countries.put(241, new Country(241, "Scotland", "UK"));
        countries.put(242, new Country(242, "Kosovo", "XK"));
        countries.put(243, new Country(243, "Eritrea", "ER"));
        countries.put(244, new Country(244, "Faroe Islands", "FO"));
        countries.put(245, new Country(245, "Seychelles", "SC"));
        countries.put(246, new Country(246, "Réunion", "RE"));
        countries.put(247, new Country(247, "Micronesia", "FM"));
        countries.put(248, new Country(248, "Yemen", "YE"));
        countries.put(253, new Country(253, "Guinea-Bissau", "GW"));
        countries.put(254, new Country(254, "São Tomé & Principe", "ST"));
        countries.put(255, new Country(255, "Northern Marianas", "MP"));
    }
}
