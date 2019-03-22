/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhongjiao.ggzy.shandong;

/**
 *
 * @author yorixh
 */
class WebJs {

    public static String js
            = "function getUrl(str) {\n"
            + "        var hh = str;\n"
            + "        var s = \"qnbyzzwmdgghmcnm\";\n"
            + "        var aa = hh.split(\"/\");\n"
            + "        var aaa = aa.length;\n"
            + "        var bbb = aa[aaa - 1].split('.');\n"
            + "        var ccc = bbb[0];\n"
            + "        var cccc = bbb[1];\n"
            + "        var r = /^\\+?[1-9][0-9]*$/;\n"
            + "        if (r.test(ccc) && cccc.indexOf('jhtml') != -1) {\n"
            + "            var srcs = parse1(ccc);\n"
            + "            var k = parse1(s);\n"
            + "            return srcs;"
            //            + "            var en = CryptoJS.AES.encrypt(srcs, k, {\n"
            //            + "                mode: CryptoJS.mode.ECB,\n"
            //            + "                padding: CryptoJS.pad.Pkcs7\n"
            //            + "            });\n"
            //            + "            var ddd = en.toString();\n"
            //            + "            ddd = ddd.replace(/\\//g, \"^\");\n"
            //            + "            ddd = ddd.substring(0, ddd.length - 2);\n"
            //            + "            var bbbb = ddd + '.' + bbb[1];\n"
            //            + "            aa[aaa - 1] = bbbb;\n"
            //            + "            var uuu = '';\n"
            //            + "            for (i = 0; i < aaa; i++) {\n"
            //            + "                uuu += aa[i] + '/'\n"
            //            + "            }\n"
            //            + "            uuu = uuu.substring(0, uuu.length - 1);\n"
            //            + "            return uuu\n"
            + "        }"
            + "};\n"
            + "function parse1(t) {\n"
            + "     return parse2(unescape(encodeURIComponent(t)))\n"
            + "};\n"
            + "function parse2(t){\n"
            + "for (var n = t.length, i = [], e = 0; e < n; e++)\n"
            + "i[e >>> 2] |= (255 & t.charCodeAt(e)) << 24 - e % 4 * 8;\n"
            + "return new init(i,n)\n"
            + "};\n"
            + "function init(t, i) {\n"
            + "t = this.words = t || [],\n"
            + "this.sigBytes = 4 * t.length\n"
            + "};\n";

}
