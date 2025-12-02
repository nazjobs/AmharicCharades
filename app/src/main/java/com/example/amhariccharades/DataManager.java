package com.example.amhariccharades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections; // Needed for shuffling
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    // Category Constants
    public static final String CAT_ANIMALS = "Animals";
    public static final String CAT_MOVIES = "Movies/TV";
    public static final String CAT_LIT = "Literature";
    public static final String CAT_PLACES = "Places";
    public static final String CAT_CELEBS = "Celebrities";
    public static final String CAT_PROVERBS = "Proverbs"; // New
    public static final String CAT_RANDOM = "Random";     // New

    // Master Data Storage
    private static final Map<String, List<String>> gameData = new HashMap<>();

    static {
        // 1. Animals
        gameData.put(CAT_ANIMALS, Arrays.asList(
                "አንበሳ", "ዝሆን", "ቀጭኔ", "ድመት", "ውሻ", "አውራሪስ", "አቦሸማኔ", "ጅብ", "ቀበሮ", "እባብ",
                "አዞ", "ጎሽ", "ዋልያ", "ድኩላ", "ጥንቸል", "ጦጣ", "ዝንጀሮ", "አህያ", "ፈረስ", "ግመል"
        ));

        // 2. Movies/TV
        gameData.put(CAT_MOVIES, Arrays.asList(
                "ፍቅር እስከ መቃብር", "ኪያ", "ሰንሰለት", "ቤቶች", "ሞጋቾች", "ዘጠኝ ለው", "ዳና", "ምን ልታዘዝ",
                "በስንቱ", "አላማ", "ታይታኒክ", "አባይ ወይስ ቬጋስ", "ረቡኒ", "ወፌ ቆመች", "ባላገሩ",
                "ፈላስፋው", "ወራሽ", "ሰው ለሰው", "ገመና", "አቦል ቲቪ"
        ));

        // 3. Literature
        gameData.put(CAT_LIT, Arrays.asList(
                "ኦሮማይ", "ዴርቶጋዳ", "ፍቅር እስከ መቃብር", "ዣንቶዣራ", "ሰሜንወርቅ", "ያ ትውልድ", "እሳት ወይስ አበባ",
                "አልወለድም", "ዮቶድ", "ራማቶሓራ", "ክቡር ድንጋይ", "ሌሊት", "ዙቤይዳ", "መሬት", "ሳቤላ",
                "ትኩሳት", "የተቆለፈበት ቁልፍ", "ሮቤል", "አሉላ አባ ነጋ", "ከአድማስ ባሻገር"
        ));

        // 4. Places
        gameData.put(CAT_PLACES, Arrays.asList(
                "ላሊበላ", "አክሱም", "ፋሲል ግንብ", "ጣና ሀይቅ", "አባይ", "ራስ ዳሽን", "ሶፍ ኦመር", "ኤርታሌ",
                "ሸገር ፓርክ", "መስቀል አደባባይ", "እንጦጦ", "ዝቋላ", "ላንጋኖ", "አዋሽ ፓርክ", "ባሌ ተራራ",
                "ሐረር ጀጎል", "ደብረ ዳሞ", "ጢስ አባይ", "ወንጪ", "ገበታ ለሀገር"
        ));

        // 5. Celebrities
        gameData.put(CAT_CELEBS, Arrays.asList(
                "ቴዲ አፍሮ", "ጥላሁን ገሰሰ", "አስቴር አወቀ", "ማህሙድ አህመድ", "አሊ ቢራ", "ሀይሌ ገብረስላሴ",
                "ጥሩነሽ ዲባባ", "ቀነኒሳ በቀለ", "ደራርቱ ቱሉ", "ንግስት ሳባ", "አፄ ቴዎድሮስ", "አፄ ሚኒሊክ",
                "ሙላቱ አስታጥቄ", "ጎሳዬ ተስፋዬ", "ኤፍሬም ታምሩ", "ሮፍናን", "ኤጃይጉ ሽባባው", "አበበ ቢቂላ",
                "ሎሬት ፀጋዬ", "አርቲስት አፈወርቅ"
        ));

        // 6. Proverbs (New)
        gameData.put(CAT_PROVERBS, Arrays.asList(
                "ቀስ በቀስ እንቁላል በእግሩ ይሄዳል", "ድር ቢያብር አንበሳ ያስር", "ከዝንጀሮ ቆንጆ ምን ይለናል",
                "ጅብ ከሄደ ውሻ ጮኸ", "ሰው መሳይ በውቀቱ", "ሲሮጡ የታጠቁት ሲሮጡ ይፈታል", "አፍ ከፈታ በሬ ከረታ",
                "ሳይደግስ አይጣላም", "ባለቤቱ ያቀለለውን አሞሌ...", "የአህያ ባል ከጅብ አያስጥልም", "ወረቀት ለብሳ አበባ መስላ",
                "እሾህን በሾህ", "ነገር በምሳሌ", "ዘንድሮና ዘንድሮ", "ዓይን ሲጠፋ", "ሁሉም ያልፋል",
                "ያልጠረጠረ ተመንጠረ", "ከምርጫው ይከለክላል", "ዶሮን ሲያታልሏት", "አኩኩሉ አልነጋም"
        ));
    }

    public static ArrayList<String> getWordsForCategory(String category) {
        // Special Logic for RANDOM
        if (category.equals(CAT_RANDOM)) {
            ArrayList<String> allWords = new ArrayList<>();
            // Loop through all keys and add everything
            for (List<String> list : gameData.values()) {
                allWords.addAll(list);
            }
            Collections.shuffle(allWords); // Shuffle the massive list
            return allWords;
        }

        // Standard Logic for specific categories
        if (gameData.containsKey(category)) {
            return new ArrayList<>(gameData.get(category));
        }
        return new ArrayList<>();
    }
}