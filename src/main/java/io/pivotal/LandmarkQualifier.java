package io.pivotal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mgoddard on 12/13/16.
 */
public class LandmarkQualifier {

    private static final double MIN_WORD_FRACTION = 0.30;

    private static final String[] WORD_LIST = {
            "according", "achieved", "achievement", "across", "action", "added", "addition",
            "adjacent", "africa", "african", "agreement", "agricultural", "airport", "alabama",
            "alberta", "album", "albums", "allows", "almost", "along", "also", "although", "america",
            "american", "among", "anatomical", "ancient", "andalusia", "angeles", "anniversary",
            "another", "appeal", "appears", "april", "arch", "architect", "architectural", "architecture",
            "area", "areas", "arizona", "around", "article", "articles", "artist", "artists",
            "arts", "associated", "association", "atlanta", "attraction", "attractions", "august",
            "australia", "australian", "author", "authored", "avenue", "award", "away", "back",
            "background", "baltimore", "band", "bank", "banksia", "base", "based", "basin",
            "battle", "beach", "became", "become", "began", "behind", "bell", "berlin", "best",
            "bien", "bill", "black", "block", "blue", "board", "book", "books", "border", "born",
            "boston", "boulevard", "branch", "brick", "bridge", "bridges", "brings", "brisbane",
            "british", "brought", "brown", "build", "building", "buildings", "built", "business",
            "butary", "calgary", "california", "called", "came", "campaign", "campus", "canada",
            "canadian", "cape", "capital", "caption", "career", "carolina", "case", "cases",
            "castle", "castles", "catchment", "category", "cathedral", "catholic", "cemetery",
            "center", "central", "centre", "centuries", "century", "change", "changed", "chapel",
            "charles", "chicago", "chief", "child", "children", "chimney", "china", "chinese",
            "christian", "church", "churches", "cinema", "citation", "cite", "cited", "cities",
            "city", "civil", "class", "classic", "clock", "close", "closed", "club", "coast",
            "code", "collection", "college", "colonial", "colorado", "columbia", "come", "commercial",
            "commission", "committee", "commons", "communities", "community", "company", "completed",
            "complex", "concert", "conference", "considered", "constitution", "constitutional",
            "constructed", "construction", "contains", "content", "control", "convert", "coord",
            "coordinates", "corner", "could", "council", "country", "county", "course", "court",
            "covered", "create", "created", "creek", "cross", "cultural", "culture", "current",
            "currently", "daily", "date", "david", "days", "deal", "death", "december", "decision",
            "decisions", "declared", "deco", "dedicated", "defaultsort", "deletion", "delhi",
            "demolished", "demolition", "department", "described", "description", "design",
            "designated", "designation", "designed", "despite", "destroyed", "detroit", "developed",
            "development", "different", "directed", "director", "display", "distance", "distinctive",
            "district", "districts", "divid", "documentary", "dome", "downtown", "dubai", "earlier",
            "early", "earth", "east", "eastern", "economy", "edition", "education", "eiffel",
            "eight", "election", "elevati", "elevation", "empire", "engineering", "england",
            "english", "entrance", "episode", "erected", "established", "establishing", "estate",
            "europe", "european", "even", "event", "events", "eventually", "ever", "every",
            "example", "exhibition", "exist", "external", "facilities", "factory", "fair", "falls",
            "familiar", "family", "famous", "farm", "father", "feature", "featured", "features",
            "february", "federal", "field", "file", "film", "films", "final", "find", "fire",
            "firm", "first", "five", "flag", "florida", "following", "force", "forest", "form",
            "former", "forms", "fort", "found", "foundation", "founded", "fountain", "four",
            "france", "francisco", "free", "freedom", "french", "front", "future", "gallery",
            "game", "games", "garden", "gardens", "gate", "gave", "general", "genre", "geographic",
            "geographical", "geography", "george", "georgia", "german", "germany", "given",
            "going", "golden", "good", "google", "gothic", "government", "grand", "great", "green",
            "ground", "group", "guadalajara", "guide", "half", "hall", "harbour", "head", "headquarters",
            "health", "heart", "height", "held", "help", "helped", "henry", "heritage", "high",
            "highest", "highly", "highway", "hill", "hills", "historic", "historical", "history",
            "hollywood", "home", "hong", "hospital", "hotel", "hotels", "house", "houses", "housing",
            "however", "html", "http", "https", "human", "icon", "iconic", "ight", "illinois",
            "image", "images", "importance", "important", "include", "included", "includes",
            "including", "independent", "india", "indian", "indiana", "industrial", "industry",
            "information", "inline", "inside", "institute", "inter", "interest", "international",
            "involved", "ireland", "isbn", "island", "issue", "issued", "issues", "italy", "jackson",
            "james", "january", "japan", "japanese", "jazz", "jersey", "john", "joseph", "journal",
            "judge", "judgement", "judgment", "july", "junction", "june", "justice", "kansas",
            "keep", "kentucky", "king", "kingdom", "known", "kong", "lake", "lakes", "land",
            "lang", "language", "large", "largest", "last", "late", "later", "laws", "lawsuit",
            "lead", "leading", "league", "left", "legal", "legislation", "length", "level",
            "library", "life", "light", "lighthouse", "like", "line", "link", "links", "list",
            "listed", "lists", "literary", "literature", "little", "live", "local", "locality",
            "locally", "located", "location", "locations", "london", "long", "longew", "longm",
            "longs", "lost", "louis", "louisville", "lower", "made", "madison", "madrid", "magazine",
            "main", "major", "make", "makes", "making", "mall", "manhattan", "mansion", "many",
            "maps", "march", "maria", "mark", "marked", "market", "martin", "mary", "maryland",
            "massachusetts", "mayor", "media", "medical", "medieval", "melbourne", "memorial",
            "mention", "mentioned", "metal", "metropolitan", "mexico", "michael", "michigan",
            "middle", "mile", "miles", "mill", "million", "missing", "mission", "mississippi",
            "model", "modern", "monastery", "monument", "monuments", "mosque", "mount", "mountain",
            "mountains", "move", "moved", "movement", "movie", "movies", "much", "mumbai", "municipal",
            "municipality", "museum", "museums", "music", "musical", "name", "named", "names",
            "nation", "national", "native", "natural", "nature", "navbox", "navigation", "nbsp",
            "ndash", "near", "nearby", "nearest", "needed", "neighborhood", "nevada", "never",
            "news", "newspaper", "next", "night", "nomination", "north", "northern", "northwest",
            "notability", "notable", "note", "noted", "novel", "november", "nowiki", "nrhp",
            "nris", "number", "numerous", "ocean", "october", "office", "official", "often",
            "ohio", "older", "oldest", "omaha", "ontario", "open", "opened", "opening", "opera",
            "order", "oregon", "original", "originally", "others", "outside", "owned", "page",
            "pages", "pakistan", "palace", "paper", "parent", "paris", "parish", "park", "parks",
            "part", "parts", "party", "pass", "passed", "passes", "passing", "past", "paul",
            "peak", "pennsylvania", "people", "performance", "period", "peter", "philadelphia",
            "photo", "picture", "pictures", "piece", "pittsburgh", "place", "places", "plans",
            "plant", "play", "played", "plaza", "plot", "point", "points", "police", "political",
            "popular", "population", "port", "portland", "position", "post", "power", "precedent",
            "present", "preservation", "preserve", "preserved", "president", "press", "primary",
            "private", "probably", "process", "produced", "production", "program", "project",
            "projects", "prominent", "properties", "property", "protected", "protection", "provided",
            "provides", "province", "public", "publication", "publications", "publishe", "published",
            "publisher", "pushpin", "quality", "queen", "quot", "race", "radio", "railway",
            "range", "reached", "real", "reason", "received", "recent", "recently", "recognizable",
            "recognized", "record", "recorded", "recording", "records", "redirect", "reference",
            "references", "reflist", "reform", "regarded", "regarding", "region", "regional",
            "register", "registered", "related", "release", "released", "religious", "remain",
            "remained", "remains", "renaissance", "report", "represented", "research", "residential",
            "residents", "responsible", "restaurant", "restoration", "restored", "result", "resulted",
            "retrieved", "review", "revival", "ridge", "right", "rights", "river", "road", "roads",
            "robert", "rock", "role", "roman", "route", "royal", "ruled", "ruling", "runs",
            "russia", "said", "saint", "santa", "scale", "scene", "school", "schools", "science",
            "sculpture", "season", "seat", "seattle", "second", "section", "seems", "seen",
            "september", "series", "serve", "served", "serves", "service", "settlement", "several",
            "shopping", "shot", "show", "shows", "side", "sign", "signed", "significance", "significant",
            "signs", "silver", "similar", "since", "singapore", "single", "site", "sites", "situated",
            "size", "skyline", "small", "smith", "social", "society", "sold", "song", "songs",
            "sound", "source", "sources", "south", "southern", "space", "spain", "spanish",
            "special", "speech", "spire", "sports", "spring", "springs", "square", "stadium",
            "stage", "stand", "standing", "stands", "star", "started", "state", "states", "station",
            "stations", "statue", "status", "steel", "still", "stirling", "stone", "stood",
            "store", "stories", "story", "street", "streets", "struct", "structure", "structures",
            "stub", "students", "studies", "studio", "study", "style", "subject", "suburb",
            "success", "successful", "summit", "support", "supreme", "surrounding", "sydney",
            "symbol", "system", "take", "taken", "takes", "talk", "tall", "tallest", "tamil",
            "tary", "team", "television", "template", "temple", "terms", "texas", "text", "theater",
            "theatre", "theory", "think", "thomas", "though", "three", "throughout", "thumb",
            "time", "Time: 209.358 ms", "times", "Timing is on.", "title", "today", "together",
            "took", "toronto", "tour", "tourism", "tourist", "tourists", "tower", "towers",
            "town", "towns", "township", "trade", "traffic", "trail", "tree", "trees", "trial",
            "tribut", "tributaries", "tributary", "twin", "type", "ukraine", "union", "unique",
            "united", "university", "unnamed", "upon", "upper", "urban", "used", "user", "using",
            "utah", "valley", "various", "venue", "version", "viccity", "victoria", "victorian",
            "victory", "video", "view", "views", "village", "virginia", "visible", "visit",
            "visited", "visitors", "visual", "volume", "vote", "wales", "walk", "washington",
            "water", "website", "well", "went", "west", "western", "white", "whose", "widely",
            "wikipedia", "william", "within", "women", "work", "worked", "working", "works",
            "world", "would", "written", "wrote", "year", "years", "york", "young", "zealand"
    };

    private static Set<String> WORD_SET;

    static {
        WORD_SET = new HashSet<String>(Arrays.asList(WORD_LIST));
    }

    public static double getWordFraction (List<String> testWordList) {
        double rv = 0.0;
        int nMatches = 0;
        for (String word : testWordList) {
            if (WORD_SET.contains(word.toLowerCase())) {
                nMatches++;
            }
        }
        rv = (double) nMatches / (double) testWordList.size();
        return rv;
    }

    public static boolean isPossibleLandmark (List<String> testWordList) {
        return getWordFraction(testWordList) >= MIN_WORD_FRACTION;
    }
}
