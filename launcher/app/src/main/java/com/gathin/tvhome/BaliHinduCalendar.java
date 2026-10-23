package com.gathin.tvhome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class BaliHinduCalendar {

    private static final Locale INDO = new Locale("in", "ID");
    private static final SimpleDateFormat FMT_DATE = new SimpleDateFormat("EEEE, d MMM", INDO);
    private static final SimpleDateFormat FMT_ISO = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    // Anchor: 2026-11-01 is Redite Sinta (day 0 of Pawukon 210-day cycle: Banyu Pinaruh)
    private static final long ANCHOR_MILLIS;

    private static final Map<Integer, String> PAWUKON_MAP = new HashMap<>();
    private static final Map<String, String> SAKA_MAP = new HashMap<>();

    static {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Makassar"));
        cal.set(2026, Calendar.NOVEMBER, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        ANCHOR_MILLIS = cal.getTimeInMillis();

        // Pawukon 210-day cycle rerahinan
        // Saraswati & Pagerwesi cycle
        PAWUKON_MAP.put(0, "Banyu Pinaruh");
        PAWUKON_MAP.put(1, "Soma Ribek");
        PAWUKON_MAP.put(2, "Sabuh Mas");
        PAWUKON_MAP.put(3, "Hari Raya Pagerwesi");

        // Enam Tumpek (Siklus 35 hari)
        PAWUKON_MAP.put(13, "Tumpek Landep");
        PAWUKON_MAP.put(48, "Tumpek Wariga (Pengatag)");
        // Day 83: Tumpek Kuningan / Hari Raya Kuningan (defined below)
        PAWUKON_MAP.put(118, "Tumpek Krulut");
        PAWUKON_MAP.put(153, "Tumpek Kandang (Uye)");
        PAWUKON_MAP.put(188, "Tumpek Wayang");

        // Rangkaian Lengkap Galungan & Kuningan
        PAWUKON_MAP.put(67, "Sugihan Jawa");
        PAWUKON_MAP.put(68, "Sugihan Bali");
        PAWUKON_MAP.put(70, "Penyekeban Galungan");
        PAWUKON_MAP.put(71, "Penyajaan Galungan");
        PAWUKON_MAP.put(72, "Penampahan Galungan");
        PAWUKON_MAP.put(73, "Hari Raya Galungan");
        PAWUKON_MAP.put(74, "Umanis Galungan");
        PAWUKON_MAP.put(78, "Pemacekan Agung");
        PAWUKON_MAP.put(80, "Buda Paing Kuningan");
        PAWUKON_MAP.put(82, "Penampahan Kuningan");
        PAWUKON_MAP.put(83, "Hari Raya Kuningan");
        PAWUKON_MAP.put(84, "Umanis Kuningan");
        PAWUKON_MAP.put(87, "Buda Cemeng Langkir");
        PAWUKON_MAP.put(108, "Pegatwakan (Cabut Penjor)");

        // Enam Buda Cemeng / Buda Wage (Bhatara Rambut Sadhana)
        PAWUKON_MAP.put(17, "Buda Cemeng Ukir");
        PAWUKON_MAP.put(52, "Buda Cemeng Warigadean");
        // Day 87: Buda Cemeng Langkir (defined above)
        PAWUKON_MAP.put(122, "Buda Cemeng Pujut");
        PAWUKON_MAP.put(157, "Buda Cemeng Menail");
        PAWUKON_MAP.put(192, "Buda Cemeng Kulawu (Rambut Sadhana)");

        // Enam Anggar Kasih (Anggara Keliwon)
        PAWUKON_MAP.put(23, "Anggar Kasih Kulantir");
        PAWUKON_MAP.put(58, "Anggar Kasih Julungwangi");
        PAWUKON_MAP.put(93, "Anggar Kasih Medangsia");
        PAWUKON_MAP.put(128, "Anggar Kasih Tambir");
        PAWUKON_MAP.put(163, "Anggar Kasih Prangbakat");
        PAWUKON_MAP.put(198, "Anggar Kasih Dukut");

        // Enam Buda Keliwon
        // Day 3: Pagerwesi (defined above)
        PAWUKON_MAP.put(38, "Buda Keliwon Gumbreg");
        // Day 73: Galungan (defined above)
        // Day 108: Pegatwakan (defined above)
        PAWUKON_MAP.put(143, "Buda Keliwon Matal");
        PAWUKON_MAP.put(178, "Buda Keliwon Ugu");

        // Penutup Siklus Watugunung
        PAWUKON_MAP.put(203, "Kajeng Keliwon Pamelastali");
        PAWUKON_MAP.put(209, "Hari Raya Saraswati");

        // Precomputed Saka calendar events (Purnama, Tilem, Nyepi, Siwaratri) 2026-2028
        SAKA_MAP.put("2026-01-03", "Purnama Kepitu");
        SAKA_MAP.put("2026-01-17", "Hari Siwaratri");
        SAKA_MAP.put("2026-01-18", "Tilem Kepitu");
        SAKA_MAP.put("2026-02-02", "Purnama Kewulu");
        SAKA_MAP.put("2026-02-16", "Tilem Kewulu");
        SAKA_MAP.put("2026-03-03", "Purnama Kesanga");
        SAKA_MAP.put("2026-03-18", "Tilem Kesanga");
        SAKA_MAP.put("2026-03-19", "Hari Raya Nyepi (Saka 1948)");
        SAKA_MAP.put("2026-04-02", "Purnama Kedasa");
        SAKA_MAP.put("2026-04-16", "Tilem Kedasa");
        SAKA_MAP.put("2026-05-01", "Purnama Jiyestha");
        SAKA_MAP.put("2026-05-16", "Tilem Jiyestha");
        SAKA_MAP.put("2026-05-31", "Purnama Sadha");
        SAKA_MAP.put("2026-06-14", "Tilem Sadha");
        SAKA_MAP.put("2026-06-29", "Purnama Kasa");
        SAKA_MAP.put("2026-07-14", "Tilem Kasa");
        SAKA_MAP.put("2026-07-29", "Purnama Karo");
        SAKA_MAP.put("2026-08-12", "Tilem Karo");
        SAKA_MAP.put("2026-08-27", "Purnama Ketiga");
        SAKA_MAP.put("2026-09-11", "Tilem Ketiga");
        SAKA_MAP.put("2026-09-26", "Purnama Kapat");
        SAKA_MAP.put("2026-10-11", "Tilem Kapat");
        SAKA_MAP.put("2026-10-25", "Purnama Kelima");
        SAKA_MAP.put("2026-11-09", "Tilem Kelima");
        SAKA_MAP.put("2026-11-24", "Purnama Kenam");
        SAKA_MAP.put("2026-12-09", "Tilem Kenam");
        SAKA_MAP.put("2026-12-23", "Purnama Kepitu");
        SAKA_MAP.put("2027-01-06", "Hari Siwaratri");
        SAKA_MAP.put("2027-01-07", "Tilem Kepitu");
        SAKA_MAP.put("2027-01-22", "Purnama Kewulu");
        SAKA_MAP.put("2027-02-06", "Tilem Kewulu");
        SAKA_MAP.put("2027-02-20", "Purnama Kesanga");
        SAKA_MAP.put("2027-03-07", "Tilem Kesanga");
        SAKA_MAP.put("2027-03-08", "Hari Raya Nyepi (Saka 1949)");
        SAKA_MAP.put("2027-03-22", "Purnama Kedasa");
        SAKA_MAP.put("2027-04-06", "Tilem Kedasa");
        SAKA_MAP.put("2027-04-20", "Purnama Jiyestha");
        SAKA_MAP.put("2027-05-05", "Tilem Jiyestha");
        SAKA_MAP.put("2027-05-20", "Purnama Mala");
        SAKA_MAP.put("2027-06-04", "Tilem Mala");
        SAKA_MAP.put("2027-06-19", "Purnama Sadha");
        SAKA_MAP.put("2027-07-03", "Tilem Sadha");
        SAKA_MAP.put("2027-07-18", "Purnama Kasa");
        SAKA_MAP.put("2027-08-02", "Tilem Kasa");
        SAKA_MAP.put("2027-08-17", "Purnama Karo");
        SAKA_MAP.put("2027-08-31", "Tilem Karo");
        SAKA_MAP.put("2027-09-15", "Purnama Ketiga");
        SAKA_MAP.put("2027-09-30", "Tilem Ketiga");
        SAKA_MAP.put("2027-10-15", "Purnama Kapat");
        SAKA_MAP.put("2027-10-29", "Tilem Kapat");
        SAKA_MAP.put("2027-11-13", "Purnama Kelima");
        SAKA_MAP.put("2027-11-28", "Tilem Kelima");
        SAKA_MAP.put("2027-12-13", "Purnama Kenam");
        SAKA_MAP.put("2027-12-28", "Tilem Kenam");
        SAKA_MAP.put("2028-01-11", "Purnama Kepitu");
        SAKA_MAP.put("2028-01-25", "Hari Siwaratri");
        SAKA_MAP.put("2028-01-26", "Tilem Kepitu");
        SAKA_MAP.put("2028-02-10", "Purnama Kewulu");
        SAKA_MAP.put("2028-02-25", "Tilem Kewulu");
        SAKA_MAP.put("2028-03-10", "Purnama Kesanga");
        SAKA_MAP.put("2028-03-25", "Tilem Kesanga");
        SAKA_MAP.put("2028-03-26", "Hari Raya Nyepi (Saka 1950)");
        SAKA_MAP.put("2028-04-09", "Purnama Kedasa");
        SAKA_MAP.put("2028-04-24", "Tilem Kedasa");
        SAKA_MAP.put("2028-05-08", "Purnama Jiyestha");
        SAKA_MAP.put("2028-05-23", "Tilem Jiyestha");
        SAKA_MAP.put("2028-06-07", "Purnama Sadha");
        SAKA_MAP.put("2028-06-22", "Tilem Sadha");
        SAKA_MAP.put("2028-07-06", "Purnama Kasa");
        SAKA_MAP.put("2028-07-21", "Tilem Kasa");
        SAKA_MAP.put("2028-08-05", "Purnama Karo");
        SAKA_MAP.put("2028-08-20", "Tilem Karo");
        SAKA_MAP.put("2028-09-04", "Purnama Ketiga");
        SAKA_MAP.put("2028-09-18", "Tilem Ketiga");
        SAKA_MAP.put("2028-10-03", "Purnama Kapat");
        SAKA_MAP.put("2028-10-18", "Tilem Kapat");
        SAKA_MAP.put("2028-11-02", "Purnama Kelima");
        SAKA_MAP.put("2028-11-16", "Tilem Kelima");
        SAKA_MAP.put("2028-12-01", "Purnama Kenam");
        SAKA_MAP.put("2028-12-16", "Tilem Kenam");
        SAKA_MAP.put("2028-12-31", "Purnama Kepitu");
    }

    private BaliHinduCalendar() {}

    public static List<String> getNextHolyDays(int maxEvents) {
        List<String> results = new ArrayList<>();
        if (maxEvents <= 0) return results;

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Makassar"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i <= 60 && results.size() < maxEvents; i++) {
            Calendar target = (Calendar) cal.clone();
            target.add(Calendar.DAY_OF_YEAR, i);
            long diffDays = Math.round((double)(target.getTimeInMillis() - ANCHOR_MILLIS) / (24 * 60 * 60 * 1000));
            int pawukonIdx = (int) (((diffDays % 210) + 210) % 210);
            boolean isKajengKeliwon = (((diffDays % 15) + 15) % 15) == 8;

            StringBuilder sb = new StringBuilder();
            Date targetDate = target.getTime();
            String isoDate = FMT_ISO.format(targetDate);
            if (SAKA_MAP.containsKey(isoDate)) {
                sb.append(SAKA_MAP.get(isoDate));
            }

            if (PAWUKON_MAP.containsKey(pawukonIdx)) {
                if (sb.length() > 0) sb.append(" & ");
                sb.append(PAWUKON_MAP.get(pawukonIdx));
            } else if (isKajengKeliwon) {
                if (sb.length() > 0) sb.append(" & ");
                sb.append("Kajeng Keliwon");
            }

            if (sb.length() > 0) {
                String eventName = sb.toString();
                String dateStr = FMT_DATE.format(targetDate);
                if (i == 0) {
                    results.add("🪔 Rahina " + eventName + " (Hari ini · " + dateStr + ")");
                } else if (i == 1) {
                    results.add("🪔 " + eventName + " (Besok · " + dateStr + ")");
                } else {
                    results.add("🪔 " + eventName + " (" + dateStr + " · " + i + " hari lagi)");
                }
            }
        }
        return results;
    }

    public static String getNextHolyDaysFormatted(int maxEvents) {
        List<String> list = getNextHolyDays(maxEvents);
        if (list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append("\n");
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public static String getNextHolyDay() {
        return getNextHolyDaysFormatted(1);
    }
}
