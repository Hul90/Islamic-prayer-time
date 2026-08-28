package com.example.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicEmeraldPrimary
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicMutedText

data class DuaItem(
    val id: String,
    val categoryId: String,
    val titleEn: String,
    val titleBn: String,
    val arabicText: String,
    val pronunciationBn: String,
    val meaningBn: String,
    val meaningEn: String,
    val source: String,
    val hadithNumber: String,
    val grade: String
)

data class DuaCategory(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val emoji: String
)

val DUA_CATEGORIES = listOf(
    DuaCategory("all", "All Duas", "সকল দোয়া", "🤲"),
    DuaCategory("sleep", "Sleep", "ঘুম", "🛌"),
    DuaCategory("wake", "Waking Up", "ঘুম থেকে ওঠা", "🌅"),
    DuaCategory("toilet", "Restroom", "টয়লেট", "🚪"),
    DuaCategory("home", "Home", "বাসা", "🏠"),
    DuaCategory("food", "Food & Drink", "খাবার", "🍽️"),
    DuaCategory("mosque", "Mosque", "মসজিদ", "🕌"),
    DuaCategory("wudu", "Wudu", "ওযু", "💧"),
    DuaCategory("travel", "Travel", "সফর", "🚗"),
    DuaCategory("morning", "Morning Dhikr", "সকাল", "☀️"),
    DuaCategory("evening", "Evening Dhikr", "সন্ধ্যা", "🌙"),
    DuaCategory("distress", "Distress / Danger", "বিপদ", "🛡️"),
    DuaCategory("anxiety", "Anxiety & Grief", "দুশ্চিন্তা", "🕊️"),
    DuaCategory("rizq", "Rizq & Debt", "রিজিক ও ঋণ", "💰"),
    DuaCategory("forgive", "Forgiveness", "ক্ষমা ও তাওবা", "🌿"),
    DuaCategory("ramadan", "Ramadan & Fasting", "রমজান ও রোজা", "🌙"),
    DuaCategory("iftar", "Iftar", "ইফতার", "🥛")
)

val DUA_LIST = listOf(
    // 1. Sleep
    DuaItem(
        "dua_sleep_1",
        "sleep",
        "Dua before Sleeping",
        "ঘুমানোর সময় পড়ার দোয়া",
        "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
        "বিসমিকাল্লাহুম্মা আমূতু ওয়া আহ্ইয়া।",
        "হে আল্লাহ! আপনার নাম নিয়ে আমি মৃত্যুবরণ (ঘুমাই) করছি এবং আপনার নামেই জীবিত (জাগ্রত) হব।",
        "In Your name O Allah, I die and I live.",
        "Sahih al-Bukhari",
        "Bukhari 6324",
        "সহীহ (Sahih)"
    ),
    // 2. Waking Up
    DuaItem(
        "dua_wake_1",
        "wake",
        "Dua after Waking Up",
        "ঘুম থেকে জাগ্রত হওয়ার দোয়া",
        "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
        "আলহামদু লিল্লাহিল্লাযী আহ্ইয়ানা বা'দা মা আমাতানা ওয়া ইলাইহিন নুশূর।",
        "সকল প্রশংসা সেই আল্লাহর যিনি আমাদেরকে মৃত্যুর (ঘুমের) পর পুনরায় জীবিত করলেন এবং তাঁরই কাছে সকলের পুনরুত্থান।",
        "All praise is for Allah who gave us life after having taken it from us and unto Him is the resurrection.",
        "Sahih al-Bukhari & Sahih Muslim",
        "Bukhari 6312, Muslim 2711",
        "সহীহ (Sahih)"
    ),
    // 3. Toilet
    DuaItem(
        "dua_toilet_enter",
        "toilet",
        "Dua before Entering Toilet",
        "টয়লেটে প্রবেশের পূর্বে দোয়া",
        "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
        "আল্লাহুম্মা ইন্নী আ'উযু বিকা মিনাল খুবুসি ওয়াল খাবা-ইছ।",
        "হে আল্লাহ! আমি আপনার কাছে পুরুষ ও নারী শয়তানের অনিষ্ট থেকে আশ্রয় প্রার্থনা করছি।",
        "O Allah, I seek refuge in You from all evil and evil-doers (male and female devils).",
        "Sahih al-Bukhari & Sahih Muslim",
        "Bukhari 142, Muslim 375",
        "সহীহ (Sahih)"
    ),
    DuaItem(
        "dua_toilet_exit",
        "toilet",
        "Dua after Exiting Toilet",
        "টয়লেট থেকে বের হওয়ার দোয়া",
        "غُفْرَانَكَ",
        "গুফরা-নাক।",
        "হে আল্লাহ! আমি আপনার কাছে ক্ষমা প্রার্থনা করছি।",
        "I seek Your forgiveness.",
        "Sunan Abi Dawud & Jami` at-Tirmidhi",
        "Abu Dawud 17, Tirmidhi 7",
        "সহীহ (Sahih)"
    ),
    // 4. Home
    DuaItem(
        "dua_home_exit",
        "home",
        "Dua when Leaving Home",
        "ঘর থেকে বের হওয়ার দোয়া",
        "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        "বিসমিল্লাহি তাওয়াক্কালতু আলাল্লাহ, লা হাওলা ওয়ালা কুওয়াতা ইল্লা বিল্লাহ।",
        "আল্লাহর নামে বের হচ্ছি, আল্লাহর ওপরই ভরসা করলাম। আল্লাহর সাহায্য ছাড়া গুনাহ থেকে বাঁচার ও নেক কাজ করার কোনো শক্তি নেই।",
        "In the name of Allah, I place my trust in Allah, there is no might nor power except with Allah.",
        "Sunan Abi Dawud & Jami` at-Tirmidhi",
        "Abu Dawud 5095, Tirmidhi 3426",
        "সহীহ (Sahih)"
    ),
    DuaItem(
        "dua_home_enter",
        "home",
        "Dua when Entering Home",
        "ঘরে প্রবেশের দোয়া",
        "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
        "বিসমিল্লাহি ওয়ালাজনা, ওয়া বিসমিল্লাহি খারাজনা, ওয়া আলাল্লাহি রাব্বিনা তাওয়াক্কালনা।",
        "আমরা আল্লাহর নামে প্রবেশ করলাম, আল্লাহর নামেই বের হয়েছিলাম এবং আমাদের প্রতিপালক আল্লাহর ওপরই ভরসা করলাম।",
        "In the name of Allah we enter, and in the name of Allah we leave, and upon our Lord we rely.",
        "Sunan Abi Dawud",
        "Abu Dawud 5096",
        "হাসান (Hasan)"
    ),
    // 5. Food
    DuaItem(
        "dua_food_before",
        "food",
        "Dua before Eating",
        "খাওয়ার শুরুতে দোয়া",
        "بِسْمِ اللَّهِ (فَإِنْ نَسِيَ: بِسْمِ اللَّهِ فِي أَوَّلِهِ وَآخِرِهِ)",
        "বিসমিল্লাহ। (শুরুতে ভুলে গেলে: 'বিসমিল্লাহি ফী আউয়ালিহী ওয়া আখিরিহী')",
        "আল্লাহর নামে শুরু করছি। (ভুলে গেলে: শুরুতে ও শেষে আল্লাহর নামে শুরু করছি)।",
        "In the name of Allah. (If forgotten: In the name of Allah at its beginning and end).",
        "Sunan Abi Dawud & Jami` at-Tirmidhi",
        "Abu Dawud 3767, Tirmidhi 1858",
        "সহীহ (Sahih)"
    ),
    DuaItem(
        "dua_food_after",
        "food",
        "Dua after Eating",
        "খাবার খাওয়ার পরের দোয়া",
        "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
        "আলহামদু লিল্লাহিল্লাযী আত'আমানা ওয়া সাক্বানা ওয়া জা'আলানা মুসলিমীন।",
        "সকল প্রশংসা আল্লাহর জন্য, যিনি আমাদেরকে আহার করালেন, পান করালেন এবং মুসলিম বানালেন।",
        "Praise belongs to Allah Who gave us food and drink, and made us Muslims.",
        "Sunan Abi Dawud & Jami` at-Tirmidhi",
        "Abu Dawud 3850, Tirmidhi 3457",
        "হাসান (Hasan)"
    ),
    // 6. Mosque
    DuaItem(
        "dua_mosque_enter",
        "mosque",
        "Dua Entering the Mosque",
        "মসজিদে প্রবেশের দোয়া",
        "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
        "আল্লাহুম্মাফ তাহ্ লী আবওয়াবা রাহমাতিকা।",
        "হে আল্লাহ! আমার জন্য আপনার রহমতের দরজাসমূহ খুলে দিন।",
        "O Allah, open for me the doors of Your mercy.",
        "Sahih Muslim",
        "Muslim 713",
        "সহীহ (Sahih)"
    ),
    DuaItem(
        "dua_mosque_exit",
        "mosque",
        "Dua Leaving the Mosque",
        "মসজিদ থেকে বের হওয়ার দোয়া",
        "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
        "আল্লাহুম্মা ইন্নী আস-আলুকা মিন ফাদলিকা।",
        "হে আল্লাহ! আমি আপনার অনুগ্রহ ও দান প্রার্থনা করছি।",
        "O Allah, I ask You from Your favor.",
        "Sahih Muslim",
        "Muslim 713",
        "সহীহ (Sahih)"
    ),
    // 7. Wudu
    DuaItem(
        "dua_wudu_after",
        "wudu",
        "Dua after Completing Wudu",
        "ওযু শেষ করার পরের দোয়া",
        "أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ، اللَّهُمَّ اجْعَلْنِي مِنَ التَّوَّابِينَ وَاجْعَلْنِي مِنَ الْمُتَطَهِّرِينَ",
        "আশহাদু আল লা ইলাহা ইল্লাল্লাহু ওয়াহদাহু লা শারীকা লাহু, ওয়া আশহাদু আন্না মুহাম্মাদান আবদুহু ওয়া রাসূলুহু। আল্লাহুম্মাজ'আলনী মিনাত তাওয়াবীন, ওয়াজ'আলনী মিনাল মুতাতাহহিরীন।",
        "আমি সাক্ষ্য দিচ্ছি আল্লাহ ছাড়া সত্য কোনো উপাস্য নেই, তিনি একক, তাঁর কোনো শরীক নেই। এবং সাক্ষ্য দিচ্ছি মুহাম্মদ (সাঃ) তাঁর বান্দা ও রাসূল। হে আল্লাহ! আমাকে তওবাকারীদের অন্তর্ভুক্ত করুন এবং পবিত্রতা অর্জনকারীদের অন্তর্ভুক্ত করুন।",
        "I testify that there is no deity except Allah alone, without partner...",
        "Sahih Muslim & Jami` at-Tirmidhi",
        "Muslim 234, Tirmidhi 55",
        "সহীহ (Sahih)"
    ),
    // 8. Travel
    DuaItem(
        "dua_travel",
        "travel",
        "Dua for Traveling",
        "বাহনে চড়ার ও সফরের দোয়া",
        "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ ۝ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنْقَلِبُونَ",
        "সুবহানাল্লাযী সাখখারা লানা হাযা ওয়া মা কুন্না লাহু মুক্বরিনীন, ওয়া ইন্না ইলা রাব্বিনা লামুনক্বালিবূন।",
        "পবিত্র সেই মহান সত্তা যিনি একে আমাদের বশীভূত করে দিয়েছেন, অথচ আমরা একে বশীভূত করতে সক্ষম ছিলাম না। এবং আমরা আমাদের রবের কাছেই প্রত্যাবর্তন করব।",
        "Glory to Him Who has subjected this to us, and we could never have it by our efforts. And verily, to Our Lord we indeed are to return.",
        "Sahih Muslim",
        "Muslim 1342",
        "সহীহ (Sahih)"
    ),
    // 9. Morning Dhikr
    DuaItem(
        "dua_morning_sayyid",
        "morning",
        "Sayyidul Istighfar (Master of Forgiveness)",
        "সায়্যিদুল ইস্তিগফার (সকালের শ্রেষ্ঠ দোয়া)",
        "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَٰهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَىٰ عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
        "আল্লাহুম্মা আনতা রব্বী লা ইলাহা ইল্লা আনতা, খালাক্বতানী ওয়া আনা আবদুকা, ওয়া আনা আলা আহ্দিকা ওয়া ওয়া'দিকা মাসতাত্বা'তু, আ'উযু বিকা মিন শাররি মা সানা'তু, আবূউ লাকা বিনি'মাতিকা 'আলাইয়্যা, ওয়া আবূউ লাকা বিযাম্বী ফাগফির লী, ফাইন্নাহু লা ইয়াগফিরুজ জুনূবা ইল্লা আনতা।",
        "হে আল্লাহ! আপনি আমার পালনকর্তা। আপনি ছাড়া সত্য কোনো উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা...",
        "O Allah, You are my Lord, there is no god but You. You have created me and I am Your servant...",
        "Sahih al-Bukhari",
        "Bukhari 6306",
        "সহীহ (Sahih)"
    ),
    // 10. Evening Dhikr
    DuaItem(
        "dua_evening_1",
        "evening",
        "Evening Protection Dua",
        "সন্ধ্যার সুরক্ষার দোয়া",
        "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
        "আমসাইনা ওয়া আমসাল মুলকু লিল্লাহ, ওয়ালহামদু লিল্লাহ, লা ইলাহা ইল্লাল্লাহু ওয়াহদাহু লা শারীকা লাহু।",
        "আমরা সন্ধ্যায় উপনীত হলাম এবং গোটা রাজত্বও আল্লাহর জন্যই সন্ধ্যায় উপনীত হলো। সকল প্রশংসা আল্লাহর...",
        "We have reached the evening and at this very evening all sovereignty belongs to Allah...",
        "Sahih Muslim",
        "Muslim 2723",
        "সহীহ (Sahih)"
    ),
    // 11. Distress
    DuaItem(
        "dua_distress_yunus",
        "distress",
        "Dua of Prophet Yunus (AS)",
        "বিপদের দোয়া (দোয়া ইউনুস)",
        "لَا إِلَٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
        "লা ইলাহা ইল্লা আনতা সুবহানাকা ইন্নী কুনতু মিনাজ জোয়ালিমীন।",
        "আপনি ছাড়া সত্য কোনো উপাস্য নেই, আপনি পরম পবিত্র! নিশ্চয়ই আমি অত্যাচারীদের অন্তর্ভুক্ত হয়ে গেছি।",
        "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
        "Jami` at-Tirmidhi",
        "Tirmidhi 3505",
        "সহীহ (Sahih)"
    ),
    // 12. Anxiety
    DuaItem(
        "dua_anxiety_grief",
        "anxiety",
        "Dua for Anxiety & Depression",
        "দুশ্চিন্তা ও ঋণগ্রস্ততার দোয়া",
        "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
        "আল্লাহুম্মা ইন্নী আ'উযু বিকা মিনাল হাম্মি ওয়াল হাযানি, ওয়াল 'আজযি ওয়াল কাসালি, ওয়াল বুখলি ওয়াল জুবনি, ওয়া দ্বালা'ইদ দাইনি ওয়া গালাবাতির রিজাল।",
        "হে আল্লাহ! আমি আপনার আশ্রয় নিচ্ছি দুশ্চিন্তা ও পেরেশানি থেকে, অক্ষমতা ও অলসতা থেকে, কৃপণতা ও কাপুরুষতা থেকে, ঋণের বোঝা ও মানুষের আধিপত্য থেকে।",
        "O Allah, I take refuge in You from anxiety and sorrow, weakness and laziness, miserliness and cowardice, the burden of debts and from being overpowered by men.",
        "Sahih al-Bukhari",
        "Bukhari 2893",
        "সহীহ (Sahih)"
    ),
    // 13. Rizq
    DuaItem(
        "dua_rizq_debt",
        "rizq",
        "Dua for Halal Rizq & Debt Relief",
        "হালাল রিজিক ও ঋণ মুক্তির দোয়া",
        "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ",
        "আল্লাহুম্মাক ফিনী বিহালালিকা 'আন হারামিকা, ওয়া আগনিনী বিফাদ্বলিকা 'আম্মান সিওয়াক।",
        "হে আল্লাহ! আমাকে আপনার হালাল রিজিকের মাধ্যমে হারাম থেকে রক্ষা করুন এবং আপনার অনুগ্রহের দ্বারা আপনি ব্যতীত অন্যদের থেকে অমুখাপেক্ষী করে দিন।",
        "O Allah, suffice me with what You have allowed instead of what You have forbidden, and make me independent of all others besides You.",
        "Jami` at-Tirmidhi",
        "Tirmidhi 3563",
        "হাসান (Hasan)"
    ),
    // 14. Forgiveness
    DuaItem(
        "dua_forgiveness",
        "forgive",
        "Dua for Sincere Forgiveness",
        "ক্ষমা প্রার্থনা ও তাওবার দোয়া",
        "أَسْتَغْفِرُ اللَّهَ الَّذِي لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ",
        "আস্তাগফিরুল্লাহাল্লাযী লা ইলাহা ইল্লা হুওয়াল হাইয়্যুল ক্বাইয়্যূমু ওয়া আতূবু ইলাইহ।",
        "আমি আল্লাহর কাছে ক্ষমা চাই, যিনি ছাড়া সত্য কোনো উপাস্য নেই, যিনি চিরঞ্জীব, চিরস্থায়ী এবং আমি তাঁর দিকেই প্রত্যাবর্তন করছি।",
        "I seek the forgiveness of Allah there is no deity except Him, the Ever-Living, the Sustainer, and I repent unto Him.",
        "Sunan Abi Dawud & Jami` at-Tirmidhi",
        "Abu Dawud 1517, Tirmidhi 3577",
        "সহীহ (Sahih)"
    ),
    // 15. Ramadan
    DuaItem(
        "dua_ramadan_crescent",
        "ramadan",
        "Dua upon Sighting the New Moon",
        "রমজান ও নতুন চাঁদ দেখার দোয়া",
        "اللَّهُمَّ أَهِلَّهُ عَلَيْنَا بِالْيُمْنِ وَالْإِيمَانِ، وَالسَّلَامَةِ وَالْإِسْلَامِ، رَبِّي وَرَبُّكَ اللَّهُ",
        "আল্লাহুম্মা আহিল্লাহু 'আলাইনা বিল-য়ুমনি ওয়াল ঈমানি, ওয়াস সালামাতি ওয়াল ইসলামি, রাব্বী ওয়া রাব্বুকাল্লাহ।",
        "হে আল্লাহ! এই চাঁদকে আমাদের ওপর বরকত, ঈমান, নিরাপত্তা ও ইসলামের সাথে উদিত করুন। (হে চাঁদ!) আমার ও তোমার রব হলেন আল্লাহ।",
        "O Allah, bring it over us with blessing and faith, and safety and Islam. My Lord and your Lord is Allah.",
        "Jami` at-Tirmidhi",
        "Tirmidhi 3451",
        "সহীহ (Sahih)"
    ),
    // 16. Iftar
    DuaItem(
        "dua_iftar_authentic",
        "iftar",
        "Authentic Dua after Iftar",
        "ইফতার করার পর পড়ার সহীহ দোয়া",
        "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ، وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ",
        "যাহাবাজ জামা'উ ওয়াবতাল্লাতিল 'উরূকু, ওয়া ছাবাতাল আজরু ইনশাআল্লাহ।",
        "পিপাসা দূর হলো, শিরা-উপশিরা সিক্ত হলো এবং ইনশাআল্লাহ পুরস্কার নির্ধারিত হলো।",
        "The thirst is gone, the veins are moistened, and the reward is confirmed, if Allah wills.",
        "Sunan Abi Dawud",
        "Abu Dawud 2357",
        "হাসান (Hasan / Sahih al-Albani)"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuasScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredDuas = remember(selectedCategory, searchQuery) {
        DUA_LIST.filter { dua ->
            val matchesCategory = selectedCategory == "all" || dua.categoryId == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    dua.titleEn.contains(searchQuery, ignoreCase = true) ||
                    dua.titleBn.contains(searchQuery, ignoreCase = true) ||
                    dua.pronunciationBn.contains(searchQuery, ignoreCase = true) ||
                    dua.meaningBn.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "সহীহ দোয়া ও জিকির ভাণ্ডার" else "Authentic Duas & Supplications",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_duas")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(if (isBangla) "দোয়া খুঁজুন (যেমন: ঘুম, সফর, ইফতার...)" else "Search Duas...")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("input_search_duas")
            )

            // Category Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DUA_CATEGORIES, key = { it.id }) { cat ->
                    val isSelected = selectedCategory == cat.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat.id },
                        label = {
                            Text("${cat.emoji} ${if (isBangla) cat.nameBn else cat.nameEn}")
                        },
                        modifier = Modifier.testTag("chip_cat_${cat.id}")
                    )
                }
            }

            // Duas List
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (filteredDuas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBangla) "কোনো দোয়া পাওয়া যায়নি" else "No duas found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = IslamicMutedText
                            )
                        }
                    }
                } else {
                    items(filteredDuas, key = { it.id }) { dua ->
                        DuaCard(dua = dua, isBangla = isBangla)
                    }
                }
            }
        }
    }
}

@Composable
private fun DuaCard(dua: DuaItem, isBangla: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isBangla) dua.titleBn else dua.titleEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic text box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = dua.arabicText,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isBangla) {
                Text(
                    text = "উচ্চারণ: ${dua.pronunciationBn}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "অর্থ: ${dua.meaningBn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicMutedText
                )
            } else {
                Text(
                    text = "Meaning: ${dua.meaningEn}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hadith Reference & Grade
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = IslamicGoldDark.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Source",
                        tint = IslamicGoldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${dua.source} (${dua.hadithNumber}) • ${dua.grade}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldDark
                    )
                }
            }
        }
    }
}
