package com.example.ui.screens.tools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicEmeraldPrimary
import com.example.ui.theme.IslamicGoldDark
import com.example.ui.theme.IslamicMutedText

data class NamazStep(
    val stepNumber: Int,
    val titleEn: String,
    val titleBn: String,
    val arabicText: String,
    val pronunciationBn: String,
    val meaningBn: String,
    val meaningEn: String,
    val hadithSource: String,
    val hadithNumber: String,
    val authenticityGrade: String,
    val scholarlyNoteBn: String = "",
    val scholarlyNoteEn: String = ""
)

val NAMAZ_STEPS = listOf(
    NamazStep(
        stepNumber = 1,
        titleEn = "Takbiratul Ihram (Opening Takbir)",
        titleBn = "তাকবীরে তাহরীমা (নামাজ শুরু)",
        arabicText = "اللَّهُ أَكْبَرُ",
        pronunciationBn = "আল্লাহু আকবার",
        meaningBn = "আল্লাহ সর্বশ্রেষ্ঠ।",
        meaningEn = "Allah is the Greatest.",
        hadithSource = "Sahih al-Bukhari & Sahih Muslim",
        hadithNumber = "Bukhari 735, Muslim 390",
        authenticityGrade = "সহীহ (Sahih - Muttafaq Alayh)",
        scholarlyNoteBn = "নামাজে প্রবেশের জন্য 'আল্লাহু আকবার' বলা আবশ্যক ফরজ।",
        scholarlyNoteEn = "Uttering the opening Takbir is an obligatory pillar of Salah."
    ),
    NamazStep(
        stepNumber = 2,
        titleEn = "Raising Hands (Raf' al-Yadayn)",
        titleBn = "হাত উঠানো (রফউল ইয়াদাইন)",
        arabicText = "رَفْعُ الْيَدَيْنِ حَذْوَ الْمَنْكِبَيْنِ",
        pronunciationBn = "রফউল ইয়াদাইনি হাযওয়াল মানকিবাঈন",
        meaningBn = "উভয় হাত কাঁধ বা কানের লতি পর্যন্ত উঠানো।",
        meaningEn = "Raising both hands to the level of shoulders or earlobes.",
        hadithSource = "Sahih al-Bukhari & Sahih Muslim",
        hadithNumber = "Bukhari 735, Muslim 390",
        authenticityGrade = "সহীহ (Sahih - Muttafaq Alayh)",
        scholarlyNoteBn = "শাফেয়ী, মালেকী, হাম্বলী ও হানাফী ফুকাহাগণের মধ্যে তাকবীরে তাহরীমার সময় হাত তোলা সুন্নাহ সম্মত।",
        scholarlyNoteEn = "Raising hands during opening Takbir is agreed Sunnah across all schools of jurisprudence."
    ),
    NamazStep(
        stepNumber = 3,
        titleEn = "Qiyam & Hand Placement",
        titleBn = "কিয়াম ও হাত বাঁধা",
        arabicText = "وَضْعُ الْيُمْنَىٰ عَلَى الْيُسْرَىٰ",
        pronunciationBn = "ওয়াদউল ইউমনা আলাল ইউসরা",
        meaningBn = "ডান হাত বাম হাতের ওপর রাখা।",
        meaningEn = "Placing the right hand over the left wrist.",
        hadithSource = "Sahih Muslim & Sunan Abi Dawud",
        hadithNumber = "Muslim 401, Abu Dawud 759",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "হানাফী মাযহাবে নাভির নিচে এবং শাফেয়ী ও হাম্বলী মাযহাবে বুকের ওপর বা নাভির ওপরে হাত বাঁধার অভিমত রয়েছে। উভয়ই সুন্নাহর ভিত্তিতে স্বীকৃত।",
        scholarlyNoteEn = "Placing hands below navel (Hanafi) or above navel/on chest (Shafi/Hanbali) are respected scholarly practices."
    ),
    NamazStep(
        stepNumber = 4,
        titleEn = "Opening Dua (Thana / Dua al-Istiftah)",
        titleBn = "সানা / দোয়া ইস্তেফতাহ",
        arabicText = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، وَتَبَارَكَ اسْمُكَ، وَتَعَالَىٰ جَدُّكَ، وَلَا إِلَٰهَ غَيْرُكَ",
        pronunciationBn = "সুবহানাকাল্লাহুম্মা ওয়া বিহামদিকা, ওয়া তাবারাকাসমুকা, ওয়া তা'আলা জাদ্দুকা, ওয়া লা ইলাহা গাইরুক।",
        meaningBn = "হে আল্লাহ! আপনার প্রশংসার সাথে পবিত্রতা ঘোষণা করছি, আপনার নাম বরকতময়, আপনার মর্যাদা সুউচ্চ এবং আপনি ছাড়া কোনো সত্য মাবুদ নেই।",
        meaningEn = "Glory be to You O Allah, and with Your praise; blessed is Your name, exalted is Your majesty, and there is no deity besides You.",
        hadithSource = "Sunan Abi Dawud & Jami` at-Tirmidhi",
        hadithNumber = "Abu Dawud 776, Tirmidhi 243",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "তাকবীরে তাহরীমার পর সূরা ফাতিহার পূর্বে সানা পাঠ করা মুস্তাহাব/সুন্নাত।",
        scholarlyNoteEn = "Reciting the opening supplication before Surah Al-Fatihah is established Sunnah."
    ),
    NamazStep(
        stepNumber = 5,
        titleEn = "Surah Al-Fatihah & Recitation",
        titleBn = "সূরা ফাতিহা ও তিলাওয়াত",
        arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝ ...",
        pronunciationBn = "বিসমিল্লাহির রাহমানির রাহীম। আলহামদু লিল্লাহি রাব্বিল আলামীন...",
        meaningBn = "সকল প্রশংসা জগৎসমূহের প্রতিপালক আল্লাহর জন্য...",
        meaningEn = "All praise is due to Allah, Lord of all the worlds...",
        hadithSource = "Sahih al-Bukhari & Sahih Muslim",
        hadithNumber = "Bukhari 756, Muslim 394",
        authenticityGrade = "সহীহ (Sahih) - 'লা সালাতা লিমান লাম ইয়াকরা বি ফাতিহাতিল কিতাব'",
        scholarlyNoteBn = "নামাজের প্রতি রাকাতে সূরা ফাতিহা পাঠ করা ওয়াজিব/রুকন।",
        scholarlyNoteEn = "Reciting Surah Al-Fatihah is an essential pillar/obligation in every unit of prayer."
    ),
    NamazStep(
        stepNumber = 6,
        titleEn = "Ruku (Bowing) & Dhikr",
        titleBn = "রুকু ও রুকুর তাসবীহ",
        arabicText = "سُبْحَانَ رَبِّيَ الْعَظِيمِ",
        pronunciationBn = "সুবহানা রাব্বিয়াল আজীম (৩ বার)",
        meaningBn = "আমার মহান প্রতিপালকের পবিত্রতা ও মহিমা বর্ণনা করছি।",
        meaningEn = "Glory be to my Lord, the Magnificent (3 times).",
        hadithSource = "Sahih Muslim & Sunan Abi Dawud",
        hadithNumber = "Muslim 772, Abu Dawud 869",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "পিঠ সোজা রেখে দুই হাত হাঁটুতে শক্ত করে ধরে প্রশান্তির সাথে রুকু করা ফরজ।",
        scholarlyNoteEn = "Bowing with straight back and hands on knees with tranquility is an obligatory pillar."
    ),
    NamazStep(
        stepNumber = 7,
        titleEn = "Rising from Ruku (Qawmah & Tasmee)",
        titleBn = "কাওমা (রুকু থেকে সোজা হয়ে দাঁড়ানো)",
        arabicText = "سَمِعَ اللَّهُ لِمَنْ حَمِدَهُ ۝ رَبَّنَا وَلَكَ الْحَمْدُ",
        pronunciationBn = "সামি'আল্লাহু লিমান হামিদাহ্ - রাব্বানা ওয়া লাকাল হামদ।",
        meaningBn = "আল্লাহ শুনেছেন যে তাঁর প্রশংসা করেছে - হে আমাদের প্রতিপালক! সকল প্রশংসা একমাত্র আপনারই।",
        meaningEn = "Allah hears the one who praises Him - Our Lord, all praise is Yours.",
        hadithSource = "Sahih al-Bukhari",
        hadithNumber = "Bukhari 795, Bukhari 796",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "রুকু থেকে উঠে সম্পূর্ণ সোজা হয়ে দাঁড়ানো এবং স্থিরতা বজায় রাখা ওয়াজিব।",
        scholarlyNoteEn = "Standing fully upright after bowing with calmness is an established obligation."
    ),
    NamazStep(
        stepNumber = 8,
        titleEn = "Sujood (Prostration) & Dhikr",
        titleBn = "সিজদা ও সিজদার তাসবীহ",
        arabicText = "سُبْحَانَ رَبِّيَ الْأَعْلَىٰ",
        pronunciationBn = "সুবহানা রাব্বিয়াল আ'লা (৩ বার)",
        meaningBn = "আমার সর্বশ্রেষ্ঠ সর্বোচ্চ প্রতিপালকের পবিত্রতা বর্ণনা করছি।",
        meaningEn = "Glory be to my Lord, the Most High (3 times).",
        hadithSource = "Sahih al-Bukhari & Sahih Muslim",
        hadithNumber = "Bukhari 812, Muslim 483",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "৭টি অঙ্গের ওপর সিজদা করা ফরজ: কপাল-নাক, উভয় হাত, উভয় হাঁটু এবং উভয় পায়ের পাতা (বুখারী ৮১২)।",
        scholarlyNoteEn = "Prostrating on seven limbs (forehead with nose, two hands, two knees, and toes) is obligatory."
    ),
    NamazStep(
        stepNumber = 9,
        titleEn = "Sitting between Two Sujoods (Jalsah)",
        titleBn = "দুই সিজদার মাঝের বৈঠক ও দোয়া",
        arabicText = "رَبِّ اغْفِرْ لِي، رَبِّ اغْفِرْ لِي",
        pronunciationBn = "রাব্বিগফির লী, রাব্বিগফির লী। (অথবা: আল্লাহুম্মাগফিরলী ওয়ারহামনী ওয়াহদিনী ওয়া আফিনী ওয়ারযুকনী)",
        meaningBn = "হে আমার পালনকর্তা! আমাকে ক্ষমা করুন, আমাকে ক্ষমা করুন।",
        meaningEn = "O my Lord, forgive me; O my Lord, forgive me.",
        hadithSource = "Sunan Abi Dawud & Sunan Ibn Majah",
        hadithNumber = "Abu Dawud 850, Ibn Majah 897",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "দুই সিজদার মাঝে স্থির হয়ে বসা এবং ক্ষমা প্রার্থনা করা অত্যন্ত সওয়াবের সুন্নাহ।",
        scholarlyNoteEn = "Sitting with calm posture between the two prostrations is a confirmed Sunnah."
    ),
    NamazStep(
        stepNumber = 10,
        titleEn = "Tashahhud (At-Tahiyyat)",
        titleBn = "তাশাহহুদ (আত্তাহিয়্যাতু)",
        arabicText = "التَّحِيَّاتُ لِلَّهِ وَالصَّلَوَاتُ وَالطَّيِّبَاتُ، السَّلَامُ عَلَيْكَ أَيُّهَا النَّبِيُّ وَرَحْمَةُ اللَّهِ وَبَرَكَاتُهُ، السَّلَامُ عَلَيْنَا وَعَلَىٰ عِبَادِ اللَّهِ الصَّالِحِينَ، أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
        pronunciationBn = "আত্তাহিয়্যাতু লিল্লাহি ওয়াস সালাওয়াতু ওয়াত তায়্যিবাতু, আসসালামু আলাইকা আইয়্যুহান নাবিয়্যু ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু, আসসালামু আলাইনা ওয়া আলা ইবাদিল্লাহিস সালিহীন, আশহাদু আল লা ইলাহা ইল্লাল্লাহু ওয়া আশহাদু আন্না মুহাম্মাদান আবদুহু ওয়া রাসুলুহু।",
        meaningBn = "যাবতীয় মৌখিক, শারীরিক ও আর্থিক ইবাদত আল্লাহর জন্য। হে নবী! আপনার প্রতি শান্তি, আল্লাহর রহমত ও বরকত বর্ষিত হোক। আমাদের প্রতি ও আল্লাহর নেক বান্দাদের প্রতি শান্তি বর্ষিত হোক। আমি সাক্ষ্য দিচ্ছি আল্লাহ ছাড়া কোনো উপাস্য নেই এবং মুহাম্মদ (সাঃ) তাঁর বান্দা ও রাসূল।",
        meaningEn = "All compliments, prayers, and pure words are due to Allah. Peace be upon you, O Prophet, and the mercy of Allah and His blessings...",
        hadithSource = "Sahih al-Bukhari & Sahih Muslim",
        hadithNumber = "Bukhari 831, Muslim 402",
        authenticityGrade = "সহীহ (Sahih - Muttafaq Alayh)",
        scholarlyNoteBn = "প্রথম ও শেষ বৈঠকে তাশাহহুদ পাঠ করা ওয়াজিব।",
        scholarlyNoteEn = "Reciting Tashahhud in the sitting position is obligatory."
    ),
    NamazStep(
        stepNumber = 11,
        titleEn = "Durood Ibrahim (Blessings on the Prophet)",
        titleBn = "দরূদে ইবরাহিম",
        arabicText = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ، اللَّهُمَّ بَارِكْ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا بَارَكْتَ عَلَىٰ إِبْرَاهِيمَ وَعَلَىٰ آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
        pronunciationBn = "আল্লাহুম্মা সাল্লি আলা মুহাম্মাদিঁও ওয়া আলা আলি মুহাম্মাদ, কামা সাল্লাইতা আলা ইবরাহিমা ওয়া আলা আলি ইবরাহিম, ইন্নাকা হামিদুম মাজীদ...",
        meaningBn = "হে আল্লাহ! মুহাম্মদ (সাঃ) এবং তাঁর পরিবারের ওপর রহমত বর্ষণ করুন, যেমন আপনি ইবরাহিম (আঃ) ও তাঁর পরিবারের ওপর রহমত বর্ষণ করেছিলেন...",
        meaningEn = "O Allah, send prayers upon Muhammad and upon the family of Muhammad, as You sent prayers upon Ibrahim...",
        hadithSource = "Sahih al-Bukhari",
        hadithNumber = "Bukhari 3370",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "শেষ বৈঠকে তাশাহহুদের পর দরূদ পাঠ করা অত্যন্ত গুরুত্বপূর্ণ সুন্নাত / রুকন।",
        scholarlyNoteEn = "Reciting Durood Ibrahim in the final sitting is an established Sunnah/pillar."
    ),
    NamazStep(
        stepNumber = 12,
        titleEn = "Dua Masura (Supplication before Salam)",
        titleBn = "দোয়া মাসূরা (সালামের পূর্বে প্রার্থনা)",
        arabicText = "اللَّهُمَّ إِنِّي ظَلَمْتُ نَفْسِي ظُلْمًا كَثِيرًا، وَلَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ، فَاغْفِرْ لِي مَغْفِرَةً مِنْ عِنْدِكَ وَارْحَمْنِي إِنَّكَ أَنْتَ الْغَفُورُ الرَّحِيمُ",
        pronunciationBn = "আল্লাহুম্মা ইন্নি জালামতু নাফসি জুলমান কাসীরা, ওয়া লা ইয়াগফিরুজ জুনুবা ইল্লা আনতা, ফাগফির লী মাগফিরাতাম মিন ইনদিকা ওয়ারহামনী, ইন্নাকা আনতাল গাফুরুর রাহীম।",
        meaningBn = "হে আল্লাহ! আমি আমার নিজের ওপর বহু অত্যাচার করেছি, আপনি ছাড়া গুনাহ ক্ষমা করার কেউ নেই। অতএব আপনার পক্ষ থেকে আমাকে মার্জনা দান করুন এবং দয়া করুন। নিশ্চয়ই আপনি পরম ক্ষমাশীল ও দয়ালু।",
        meaningEn = "O Allah, I have wronged myself greatly and none can forgive sins except You...",
        hadithSource = "Sahih al-Bukhari & Sahih Muslim",
        hadithNumber = "Bukhari 834, Muslim 2705",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "হযরত আবু বকর সিদ্দিক (রাঃ)-কে মহানবী ﷺ এই দোয়াটি নামাজের মধ্যে পাঠ করতে শিখিয়েছিলেন।",
        scholarlyNoteEn = "Taught directly by the Prophet ﷺ to Abu Bakr as-Siddiq (RA) for Salah."
    ),
    NamazStep(
        stepNumber = 13,
        titleEn = "Tasleem (Concluding Salam)",
        titleBn = "সালাম ফেরানো (নামাজ সমাপ্তি)",
        arabicText = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
        pronunciationBn = "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহ",
        meaningBn = "আপনাদের প্রতি শান্তি এবং আল্লাহর রহমত বর্ষিত হোক।",
        meaningEn = "Peace and mercy of Allah be upon you.",
        hadithSource = "Sahih Muslim & Sunan Abi Dawud",
        hadithNumber = "Muslim 582, Abu Dawud 996",
        authenticityGrade = "সহীহ (Sahih)",
        scholarlyNoteBn = "ডানে ও বামে সালাম ফেরানোর মাধ্যমে নামাজ সমাপ্ত হয়। প্রথম সালাম ফেরানো ফরজ/রুকন।",
        scholarlyNoteEn = "Turning the head right and left with Salam completes the prayer."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamazLearningScreen(
    isBangla: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBangla) "সহীহ নামাজ শিক্ষা ও হাদীস দলিল" else "Authentic Prayer Guide & Hadith",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_namaz_learn")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = IslamicEmeraldPrimary.copy(alpha = 0.10f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Authentic",
                            tint = IslamicEmeraldPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isBangla) {
                                "রাসূলুল্লাহ ﷺ বলেছেন: 'তোমরা সেভাবে নামাজ আদায় করো যেভাবে আমাকে নামাজ আদায় করতে দেখেছো।' (সহীহ বুখারী ৬৩১)"
                            } else {
                                "The Prophet ﷺ said: 'Pray as you have seen me praying.' (Sahih al-Bukhari 631)"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmeraldPrimary
                        )
                    }
                }
            }

            items(NAMAZ_STEPS, key = { it.stepNumber }) { step ->
                NamazStepCard(step = step, isBangla = isBangla)
            }
        }
    }
}

@Composable
private fun NamazStepCard(step: NamazStep, isBangla: Boolean) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(IslamicEmeraldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = step.stepNumber.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isBangla) step.titleBn else step.titleEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = IslamicEmeraldPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic text box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = step.arabicText,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold,
                    color = IslamicEmeraldDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pronunciation & Meaning
            if (isBangla) {
                Text(
                    text = "উচ্চারণ: ${step.pronunciationBn}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "অর্থ: ${step.meaningBn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = IslamicMutedText
                )
            } else {
                Text(
                    text = "Meaning: ${step.meaningEn}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hadith Reference Badge
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
                        contentDescription = "Reference",
                        tint = IslamicGoldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "দলিল: ${step.hadithSource} (${step.hadithNumber}) • ${step.authenticityGrade}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldDark
                    )
                }
            }

            // Expandable Scholarly Fiqh Explanation
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBangla) "ফিকহী বিবরণ ও সতর্কতা:" else "Scholarly Notes:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = IslamicEmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla) step.scholarlyNoteBn else step.scholarlyNoteEn,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
