package edu.zjut.contactsMaterial3Experimental.Helpers

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType

object InitialPinyinHelper {
    private lateinit var format:HanyuPinyinOutputFormat

    fun chineseToSpell(chineseString: String): String{
        return if (chineseString.isNotEmpty()){
            format= HanyuPinyinOutputFormat()
            format.caseType= HanyuPinyinCaseType.LOWERCASE
            format.toneType= HanyuPinyinToneType.WITHOUT_TONE
            format.vCharType= HanyuPinyinVCharType.WITH_V
            PinyinHelper.toHanYuPinyinString(chineseString, format, "", false)
        }else{
            ""
        }
    }
}