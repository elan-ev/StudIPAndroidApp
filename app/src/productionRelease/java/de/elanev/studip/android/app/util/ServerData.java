/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.util;

/**
 * Created by joern on 31.10.13.
 */
public final class ServerData {
    public final static String serverJson =
            "{"
            + "  \"servers\": ["
            + "    {"
            + "      \"name\": \"Universität Osnabrück\","
            + "      \"consumer_key\": \"e4450a85a3d48162ab5fcae59c219992052600ca4\","
            + "      \"consumer_secret\": \"0d713fcc36bcf5a5bdf0043745c2dd48\","
            + "      \"base_url\": \"https://studip.uni-osnabrueck.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipmobil@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Universität Oldenburg\","
            + "      \"consumer_key\": \"8f882dc7b707a1896718aafadd86284c0526e7362\","
            + "      \"consumer_secret\": \"07b2d3a8faf0cce433a6f84424f8bcd8\","
            + "      \"base_url\": \"https://elearning.uni-oldenburg.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipsupport@uni-oldenburg.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Universität Vechta\","
            + "      \"consumer_key\": \"aea2d579e27be537aa6dd021714b60da054bf9e6b\","
            + "      \"consumer_secret\": \"83bd390b7f42144350ee21e4d0e0adb0\","
            + "      \"base_url\": \"https://studip.uni-vechta.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"it-support@uni-vechta.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Universität Bremen\","
            + "      \"consumer_key\": \"8f3992118e8afe1a6146c2c41b04da730527b7d8e\","
            + "      \"consumer_secret\": \"4745f0dee3296b7536fa7600f8e38ea5\","
            + "      \"base_url\": \"https://elearning.uni-bremen.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"info@elearning.uni-bremen.de\""
            + "    },"
            //+ "    {"
            //+ "      \"name\": \"TU Braunschweig\","
            //+ "      \"consumer_key\": \"edcc6fb2b8b1ee53c5cd5ecb4537775c0568d1fa8\","
            //+ "      \"consumer_secret\": \"41e7e20d3e189b2bd4f97dc7880dbd86\","
            //+ "      \"base_url\": \"https://studip.tu-braunschweig.de/plugins.php/restipplugin\","
            //+ "      \"contact_email\": \"studip@tu-braunschweig.de\""
            //+ "    },"
            + "    {"
            + "      \"name\": \"Universität Hannover\","
            + "      \"consumer_key\": \"1ed643f2fc9a7ffebe3b5fa4dfa6f368053f1f0aa\","
            + "      \"consumer_secret\": \"210e245092eb84994b65f108357e51a0\","
            + "      \"base_url\": \"https://studip.uni-hannover.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"elearning@uni-hannover.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Universität Passau\","
            + "      \"consumer_key\": \"8f18216c88059175df2ff131fd1c4ba9053c79046\","
            + "      \"consumer_secret\": \"9d8450a25cc4c808d575384186afe372\","
            + "      \"base_url\": \"https://studip.uni-passau.de/studip/plugins.php/restipplugin\","
            + "      \"contact_email\": \"support@intelec.uni-passau.de\""
            + "    },"
            //+ "    {"
            //+ "      \"name\": \"Universität Bielefeld\","
            //+ "      \"consumer_key\": \"19e5c952599b54e030ede1b4372510af0565c134e\","
            //+ "      \"consumer_secret\": \"383a195b46bd3cefdfab91481c837557\","
            //+ "      \"base_url\": \"https://elearning.uni-bielefeld.de/studip/plugins.php/restipplugin\","
            //+ "      \"contact_email\": \"studip.ub@uni-bielefeld.de\""
            //+ "    },"
            + "    {"
            + "      \"name\": \"Universität Gießen\","
            + "      \"consumer_key\": \"be5e3787ed7c974a633899fdbf5b00b00531dc92e\","
            + "      \"consumer_secret\": \"3bcefb075f15c9c042e17415a56a1d3f\","
            + "      \"base_url\": \"https://studip.uni-giessen.de/studip/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studip@uni-giessen.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Universität Halle\","
            + "      \"consumer_key\": \"9b5654eb205f3890155cfb55bdff536d0534d2c33\","
            + "      \"consumer_secret\": \"1eda81cb952baff56833c4c8402683a5\","
            + "      \"base_url\": \"https://studip.uni-halle.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"admin@studip.uni-halle.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Universität Trier\","
            + "      \"consumer_key\": \"ceb6b731ff243462c8d17b4987f57a9d05360cc2a\","
            + "      \"consumer_secret\": \"1791c032dc8d776199a9471f57ec96a7\","
            + "      \"base_url\": \"https://studip.uni-trier.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studip@uni-trier.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Universität Rostock\","
            + "      \"consumer_key\": \"b4bd27f1efb91e037538fb4910ef15bf0537afc6a\","
            + "      \"consumer_secret\": \"dc40d7f770be525e94170b0b32400852\","
            + "      \"base_url\": \"https://studip.uni-rostock.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studip-support@uni-rostock.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"HS Wismar\","
            + "      \"consumer_key\": \"9477ef9c5f52e0b44a53c2c5a10a40cf0540d9a17\","
            + "      \"consumer_secret\": \"336dae985495a677f415789c32063c80\","
            + "      \"base_url\": \"https://studip.hs-wismar.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studip-support@hs-wismar.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"EAH Jena / FB SW\","
            + "      \"consumer_key\": \"fbb7111145b292482b28fad12f51285a05303b537\","
            + "      \"consumer_secret\": \"e056bc22a96058717cdaf86536c3ffd2\","
            + "      \"base_url\": \"https://studip.sw.fh-jena.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studip24@sw.fh-jena.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"PH Schwäbisch Gmünd\","
            + "      \"consumer_key\": \"8962ffefc64a42a2c1c5a6e2c82c302105328545c\","
            + "      \"consumer_secret\": \"4512dd245daa313849097f70cc4902e7\","
            + "      \"base_url\": \"https://lms.ph-gmuend.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"e-learning@ph-gmuend.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"HAWK HHG\","
            + "      \"consumer_key\": \"1799e8dc61a3cf6dff05186714c5178b0532ad494\","
            + "      \"consumer_secret\": \"df554367405af6180badc1c7213d6047\","
            + "      \"base_url\": \"https://studip.hawk-hhg.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"hildesheim@studip.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"HfWU Nürtingen-Geislingen\","
            + "      \"consumer_key\": \"50e0068e1f94678ab7d99168f7b34b0a053318db4\","
            + "      \"consumer_secret\": \"9541e31c5c83d73093ce76a288f29592\","
            + "      \"base_url\": \"https://neo.hfwu.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"neo@hfwu.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"HS Ostfalia\","
            + "      \"consumer_key\": \"61fb86097b20cf436b40ff95d65297de0536787ea\","
            + "      \"consumer_secret\": \"31560843b63feb5f6ee5bac69b8da9d1\","
            + "      \"base_url\": \"https://studip.ostfalia.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"rechenzentrum@ostfalia.de\""
            + "    },"
            //+ "    {"  //->defect
            //+ "      \"name\": \"HBK Braunschweig\","
            //+ "      \"consumer_key\": \"f855fe56f1aebedf7dbb980279ad52dc053679820\","
            //+ "      \"consumer_secret\": \"b78266e68607a7e026d4bc7ef5ad6e88\","
            //+ "      \"base_url\": \"https://studip.hbk-bs.de/plugins.php/restipplugin\","
            //+ "      \"contact_email\": \"studip@hbk-bs.de\""
            //+ "    }," //->defect
            + "    {"
            + "      \"name\": \"EHS Dresden\","
            + "      \"consumer_key\": \"49d1716d543d4c9e6b04afc7a84414d8055d45deb\","
            + "      \"consumer_secret\": \"99acd831b7b8f3d4eb32737b288e5e37\","
            + "      \"base_url\": \"http://studip.ehs-dresden.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studip@ehs-dresden.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"eL4 Projekt\","
            + "      \"consumer_key\": \"b75c05d43cf5b6316a8d482894b81b9d052723d68\","
            + "      \"consumer_secret\": \"2ba96f74f9ea33e9c329024f064674ff\","
            + "      \"base_url\": \"http://el4.elan-ev.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipmobil@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Stud.IP Developer Server\","
            + "      \"consumer_key\": \"b713cfcc739715c187fb3ff88e8f68d30524bfdd5\","
            + "      \"consumer_secret\": \"2b291630016fd00a232e2e1aa9fec5b7\","
            + "      \"base_url\": \"http://develop.studip.de/studip/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipmobil@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"VHS Osnabrück\","
            + "      \"consumer_key\": \"08429698f790c23f4a83658357b3d0ef0568bb357\","
            + "      \"consumer_secret\": \"3ade9514c3e552cf06da2c57bc4b5095\","
            + "      \"base_url\": \"http://osnabrueck.elan-ev.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"el4@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"VHS Lingen\","
            + "      \"consumer_key\": \"f2e971435831c2db685718c90be469c10537b295d\","
            + "      \"consumer_secret\": \"c704335d9e393adf6611078dfc90cfb6\","
            + "      \"base_url\": \"http://lingen.elan-ev.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipmobil@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"VHS Papenburg\","
            + "      \"consumer_key\": \"dfab90feb3ec4b91df772ee834d200090537b2b66\","
            + "      \"consumer_secret\": \"4dd5ad2710e3ca49cd605219c1dca7ce\","
            + "      \"base_url\": \"http://papenburg.elan-ev.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipmobil@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"VHS Ammerland\","
            + "      \"consumer_key\": \"68a368068a504cdba9bb0a03673fcfb70537b2c0b\","
            + "      \"consumer_secret\": \"084b178978600f939d862fdb620e0fa1\","
            + "      \"base_url\": \"http://ammerland.elan-ev.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipmobil@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"VHS Meppen\","
            + "      \"consumer_key\": \"ff9f5d09edf124d86375b5fc0262fbff0537dbac8\","
            + "      \"consumer_secret\": \"66132bce68d1e1efd18d53c8647a962c\","
            + "      \"base_url\": \"http://meppen.elan-ev.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipmobil@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"VHS Norden\","
            + "      \"consumer_key\": \"9a41be2d93d27c9d27ca965dceec72820537dbb91\","
            + "      \"consumer_secret\": \"9d81ee064ab21065c22553e987e614e4\","
            + "      \"base_url\": \"http://norden.elan-ev.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"studipmobil@elan-ev.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"Herman Nohl Schule\","
            + "      \"consumer_key\": \"d8d176f55d078c0135eff861f7534d1905432c6cf\","
            + "      \"consumer_secret\": \"953110046045aa3ba312b9c1e5de24b6\","
            + "      \"base_url\": \"http://hermannohl-online.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"sekretariat@herman-nohl-schule.de\""
            + "    },"
            + "    {"
            + "      \"name\": \"BA GSS\","
            + "      \"consumer_key\": \"e5953bbc43f58d0370d5b9f88f210739054cf9b11\","
            + "      \"consumer_secret\": \"5154b31ef40e47d7d3c7524f2d51a177\","
            + "      \"base_url\": \"http://studip.shg-schule.de/plugins.php/restipplugin\","
            + "      \"contact_email\": \"f.guthoerl@sb.shg-kliniken.de\""
            + "    }"
            + "  ]"
            + "}";
}