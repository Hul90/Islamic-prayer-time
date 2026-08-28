package com.example.model

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val countryName: String,
    val timeZoneId: String,
    val isAutoDetected: Boolean = true
) {
    fun displayLocation(isBangla: Boolean = false): String {
        return if (countryName.isNotBlank() && countryName != cityName) {
            "$cityName, $countryName"
        } else {
            cityName
        }
    }

    companion object {
        val DEFAULT_DHAKA = LocationData(
            latitude = 23.8103,
            longitude = 90.4125,
            cityName = "Dhaka",
            countryName = "Bangladesh",
            timeZoneId = "Asia/Dhaka",
            isAutoDetected = false
        )

        // Comprehensive Bangladesh 64 Districts + Key Towns
        val BANGLADESH_DISTRICTS = listOf(
            LocationData(23.8103, 90.4125, "Dhaka (ঢাকা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.8950, 90.4043, "Tongi (টঙ্গী)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.9999, 90.4203, "Gazipur (গাজীপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.3569, 91.7832, "Chattogram (চট্টগ্রাম)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.8949, 91.8687, "Sylhet (সিলেট)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.3636, 88.6241, "Rajshahi (রাজশাহী)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.8456, 89.5403, "Khulna (খুলনা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.7010, 90.3535, "Barisal (বরিশাল)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(25.7439, 89.2752, "Rangpur (রংপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.7471, 90.4203, "Mymensingh (ময়মনসিংহ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.4682, 91.1788, "Cumilla (কুমিল্লা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.8465, 89.3777, "Bogura (বগুড়া)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(21.4272, 92.0058, "Cox's Bazar (কক্সবাজার)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.9999, 90.4203, "Gazipur (গাজীপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.6238, 90.5000, "Narayanganj (নারায়ণগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.2513, 89.9167, "Tangail (টাঙ্গাইল)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.6071, 89.8429, "Faridpur (ফরিদপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.1664, 89.2081, "Jashore (যশোর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.9088, 89.1220, "Kushtia (কুষ্টিয়া)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.0064, 89.2372, "Pabna (পাবনা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(25.6217, 88.6355, "Dinajpur (দিনাজপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.8246, 91.0995, "Noakhali (নোয়াখালী)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.2321, 90.6631, "Chandpur (চাঁদপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.9571, 91.1119, "Brahmanbaria (ব্রাহ্মণবাড়িয়া)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.4331, 90.7866, "Kishoreganj (কিশোরগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.9375, 89.9378, "Jamalpur (জামালপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.3745, 91.4155, "Habiganj (হবিগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.4829, 91.7774, "Moulvibazar (মৌলভীবাজার)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(25.0658, 91.4073, "Sunamganj (সুনামগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(25.0315, 89.0279, "Joypurhat (জয়পুরহাট)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.7936, 88.9318, "Naogaon (নওগাঁ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.4102, 88.9890, "Natore (নাটোর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.5965, 88.2775, "Chapai Nawabganj (চাঁপাইনবাবগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.4534, 89.7006, "Sirajganj (সিরাজগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.6533, 89.7853, "Bagerhat (বাগেরহাট)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.5422, 88.8556, "Jhenaidah (ঝিনাইদহ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.4855, 89.4198, "Magura (মাগুরা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.1625, 89.5050, "Narail (নড়াইল)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.7185, 89.0705, "Satkhira (সাতক্ষীরা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.6440, 88.8556, "Chuadanga (চুয়াডাঙ্গা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.7622, 88.6318, "Meherpur (মেহেরপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.3551, 90.0401, "Barguna (বরগুনা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.6859, 90.6481, "Bhola (ভোলা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.5691, 90.1870, "Jhalokati (ঝালকাঠি)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.3596, 90.3298, "Patuakhali (পটুয়াখালী)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.5841, 89.9720, "Pirojpur (পিরোজপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.9749, 91.3992, "Feni (ফেনী)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.9447, 90.8282, "Lakshmipur (লক্ষ্মীপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.1953, 92.2184, "Bandarban (বান্দরবান)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.1193, 91.9847, "Khagrachhari (খাগড়াছড়ি)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(22.6574, 92.1754, "Rangamati (রাঙ্গামাটি)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(25.3285, 89.5424, "Gaibandha (গাইবান্ধা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(25.8072, 89.6295, "Kurigram (কুড়িগ্রাম)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(25.9923, 89.2847, "Lalmonirhat (লালমনিরহাট)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(25.9318, 88.8560, "Nilphamari (নীলফামারী)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(26.3330, 88.5532, "Panchagarh (পঞ্চগড়)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(26.0337, 88.4617, "Thakurgaon (ঠাকুরগাঁও)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.2333, 89.8667, "Gopalganj (গোপালগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.1641, 90.1897, "Madaripur (মাদারীপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.5422, 90.5305, "Munshiganj (মুন্সীগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.8644, 90.0047, "Manikganj (মানিকগঞ্জ)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.7570, 89.6445, "Rajbari (রাজবাড়ী)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.2423, 90.4348, "Shariatpur (শরীয়তপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.9036, 90.1770, "Sherpur (শেরপুর)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(24.8837, 90.7285, "Netrokona (নেত্রকোণা)", "Bangladesh", "Asia/Dhaka", false),
            LocationData(23.9989, 90.7197, "Narsingdi (নরসিংদী)", "Bangladesh", "Asia/Dhaka", false)
        )

        val MAJOR_WORLD_CITIES = listOf(
            LocationData(21.4225, 39.8262, "Makkah", "Saudi Arabia", "Asia/Riyadh", false),
            LocationData(24.4672, 39.6024, "Madinah", "Saudi Arabia", "Asia/Riyadh", false),
            LocationData(31.7683, 35.2137, "Jerusalem", "Palestine", "Asia/Jerusalem", false),
            LocationData(24.7136, 46.6753, "Riyadh", "Saudi Arabia", "Asia/Riyadh", false),
            LocationData(25.2048, 55.2708, "Dubai", "UAE", "Asia/Dubai", false),
            LocationData(30.0444, 31.2357, "Cairo", "Egypt", "Africa/Cairo", false),
            LocationData(41.0082, 28.9784, "Istanbul", "Turkey", "Europe/Istanbul", false),
            LocationData(3.1390, 101.6869, "Kuala Lumpur", "Malaysia", "Asia/Kuala_Lumpur", false),
            LocationData(-6.2088, 106.8456, "Jakarta", "Indonesia", "Asia/Jakarta", false),
            LocationData(51.5074, -0.1278, "London", "United Kingdom", "Europe/London", false),
            LocationData(40.7128, -74.0060, "New York", "United States", "America/New_York", false),
            LocationData(28.6139, 77.2090, "New Delhi", "India", "Asia/Kolkata", false),
            LocationData(24.8607, 67.0011, "Karachi", "Pakistan", "Asia/Karachi", false),
            LocationData(31.5204, 74.3587, "Lahore", "Pakistan", "Asia/Karachi", false),
            LocationData(1.3521, 103.8198, "Singapore", "Singapore", "Asia/Singapore", false),
            LocationData(35.6762, 139.6503, "Tokyo", "Japan", "Asia/Tokyo", false),
            LocationData(-33.8688, 151.2093, "Sydney", "Australia", "Australia/Sydney", false),
            LocationData(43.6532, -79.3832, "Toronto", "Canada", "America/Toronto", false)
        )
    }
}
