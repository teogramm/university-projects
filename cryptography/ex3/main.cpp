#include <boost/regex.hpp>
#include <unordered_set>
#include <iostream>
#include <unordered_map>
#include <queue>

/*
 * Vigenere decrypter - instructions from https://inventwithpython.com/hacking/chapter21.html
 */

using string = std::string;
using set = std::unordered_set<unsigned>;
typedef std::pair<unsigned,unsigned> pair;

string text = R"(
MYHSIFPFGIMUCEXIPRKHFFQPRVAGIDDVKVRXECSKAPFGHMESJWUSSEHNEZIXFFLPQDVTCEUGTEEMFRQXWYCL
PPAMBSKSTTPGSMIDNSESZJBDWJWSPQYINUVRFXPVPCEOZQRBNLUIINSRPXLEEHKSTTPGCEIMCSKVVVTJQRBS
IUCKJOIIXXOVHYEFLINOEXFDPZJVFKTETVFXTTVJVRTBXRVGJRAIFPSRGTDXYSIWYXWVFPAQSSEHNEZIXFVR
XQPRURVWBXWVCEIMCSKVVVUCXYWJAAGPUHYIDTMJFFSYUSISMIDNSESRRPILVUFSPTEIHYMEGMTVRRPREEDI
SHXHVTFVQKIIMFRQILVKRCAUPZTVGMCFVTIIQPRUPVEGIMWICFGIAVVRZQASJHKLQLEPUIIQSLRGGSUHSESU
QQCWJCLPEWEJPRVDXGRRVHFWINCIPPLMKVYEFTLRGXSAHIJHVTBTHLGZRFDQZGVVKPRUPCSASWYSUAQWEMSU
IHTPFDVHEEIVRSYITLRJVWTJXFIIWQAZVGZRYPGYWEIDNXYOKKUKIJOSYZSEEQVLMHPVTKYEXRNOEXAJVBBF
AXTHXSYEEBEUSLWONRZQRPAJVTZVZQGRVGJLMGHRBUYZZMERNIFWMEYKSABYTVRRPUIVZKSAAMKHCIYDVVHY
EZBETVZRQGCNSEIQSLLARRUICDCIIFWEEQCIHTVESJWITRVSUOUCHESJWMCHXSEXXTRVGJAUILFIKXTTWVEL
EXXXZSJPUUINWCPNTZZCCIZIEERRPXLMCZSIXDWKHYIMTVFDCEZTEERKLQGEUWFLMKISFFYSWXLGTPAHIIHF
KQILVFKLQKIIMEEFJVVCWXTTWVWEZQCXZCEWOGMVGFYFUSIHYISDSUBVWEXRDSEGDXIJCLXRDVLBZZQGWRZS
VAILVFYSASJFFKLQJRZHPSRJWRZCIHTRECNQKKSZQVMEGIRQYMZVQZZCMACWKVISGVLFIKXTTAFFCHYXPCWF
REDJUSJTMXVZBXQQCAFAVRMCHCWKXXTGYWCHDTRMWTXUBWFTRWKHXVAKLMIQRYVWYTRKCIXGGIRBUMYEVZGF
RUCRFQVRFEIFDCIFDXYCJIIWSTOELQPVDSZWMNHFBFXPTWGOZVFWIDWJIDNXYOKMECSNIGSZJWZGSYFILVDR
WEXRXCWKDTIUHYINXXKSIRQHWFTDIZLLFTVEDILVKRCAULLARRBGSXFVWEILVVRXQDJDSEAUAPGOJWMCHUWT
XMISIGUMQPRUHYIBDAVFKLQNXFCBJDDQKVVTQDTCSNMXAVVHLVZISKVVTQDTCSRRPHSCCEKMHQVBUMQAMSSI
XKLMCZEIHTVGSIMEWWFZUMQGWUCEXSXZVMFYDHICJVWFDFIIKIEBIEKYSPTWGWJIKDYVBJPMKIPCLATDVVUZ
QQCXPCLVXXZVGKIXACFINLMIXFRFATPXKCKLUCORBUATPXKCWIQAAYCUVUAPPCLHUTXPCLXDTEKMFYXXOVQR
XFAILGVCAJEJQRRZDRWCUHQGHFBKKUKIPCLVETPMSJXAILVGVYZCEKIIEXBIEARGTXRVAVRIXXYARGTXRVAZ
RPHEERDEOWMESYIMGXJMFYMGIECKQMRLZBVWKDYRFVRAIGRHKPQNSLOIIYTRPCLLMKIKVVPAKIFTYYYPRZHP
MZNSLFYIMGXJMFYPDRKVRXQDRCMKLQJRCCMIPWEKSKLQJRCCMIPPRUHYIGCRRHLVMAWFZUMQGWUCEXRXKYHW
SDHPRJVVKUMXVKJAGPZPVVFNMEHYIETZVBKLOWEGHVVAUWKZLOQXXZGNVUIXVBKLQZMEUUSYDJXCUMELMKVZ
RYPRECKSZTQRBESDPKICLTAUQVBSYFXRRZCQQCMEMFYKDYKVVTQDTCSYEHTXYSGSITVKVVTALIIHFGDTEKSD
EOWMESJXTTTFKVVFDGISRXQWEGDZRQHWPCLXTTTVCGPQWEMSKLQESNSIXABEBSKLUHPZTVJDTIRBUFQPYKWW
YXISDOBIFWMJZZJQPAFBUIDUYCOUZQCXLFVXTTRZBKLQCEDSFJPTQFQIEONPVHLWGHIKVRXBDAVFCIFJWRZC
YZXXVZVXGHJZUYXRDVRBVAIDVCRRHQRIEHNSDAHKVRXIXPCUZZQBIEOTLMCGVHFAAGOKVRXIXPCUZZQNSLHY
ERJXLFVEZSSCRRKQPWVQLVUICSMKLQEVFAZWQDJKVVWQILZBXWNGYKSJLMKIIWJIZISGCNIDQYKHYIKAMVHY
IKSSECKJGAJZZKLMITICDMETXYSPRQKIIKZPXSMTHRXAGWWFVIFWIDGVPHTWSIKXTTCVBJPMKIKVVTQDTCSE
SIAIKIJJUVLKHFJGAJZZKLMITICDMETPVHLWRXKYHKSRGIVHYIIDVCRKSPDENOPAUILEOKMACECPRVDXIIGK
SPDENOPAUILXFVIPLMKVYEFTEERZRFDPVFRROTPVHLWRXKYHWSDPAFFCHAUVVOJSZPAFFCHIWIISJGUTRTSR
RPEVFUIIEHAZZCPQPHKCRPXBIEGYEBEMESJWEDPUWVVEXRKVVRMBIFTUIYDGIOTCXTXLGRPXJRZHV)";

std::array<double,26> english_frequencies = {
0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015,
0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749,
0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758,
0.00978, 0.02360, 0.00150, 0.01974, 0.00074};

std::vector <unsigned > factors(unsigned x) {
    // Code from https://www.rookieslab.com/posts/most-efficient-way-to-find-all-factors-of-a-number-python-cpp#efficient-c-implementation-to-find-factors-of-a-number
    // We will store all factors in `result`
    std::vector <unsigned> result;
    int i = 1;
    // This will loop from 1 to int(sqrt(x))
    while(i*i <= x) {
        // Check if i divides x without leaving a remainder
        if(x % i == 0) {
            result.push_back(i);
            // Handle the case explained in the 4th
            if(x/i != i) {
                result.push_back(x/i);
            }
        }
        i++;
    }
    // Return the list of factors of x
    return result;
}


set findRepeats(const std::string &message, unsigned length){
    set distances;
    for(unsigned i=0; i<message.size()-length; i++) {
        auto searchString = message.substr(i,length);
        auto searchRegex = boost::regex(searchString);
        boost::match_results<std::string::const_iterator> what;
        // Search only after the text.
        auto start = message.cbegin()+i+length;
        while(boost::regex_search(start, message.cend(),what,searchRegex)){
            // Count the distance between the beginning of each group
            auto distance = std::distance(start-length,what[0].first);
//            std::cout << what.str() << " " << distance << '\n';
            distances.insert(distance);
            // Move start to just after the match
            start = what[0].second;
        }
    }
    return distances;
}

/**
 * Get the count of all the factors of the distances.
 */
std::unordered_map<unsigned, unsigned> getFactorCount(const set &distances){
    auto counts = std::unordered_map<unsigned, unsigned>();
    for(auto distance: distances){
        for(auto factor: factors(distance)) {
            // Insert returns an iterator if the key already exists
            if(factor > 2) {
                auto counter = counts.insert(std::unordered_map<unsigned, unsigned>::value_type(factor, 0));
                counter.first->second++;
            }
        }
    }
    return counts;
}

/**
 * Get Every Nth Letters from a String.
 * return[0] contains every Nth letter starting from the first letter
 * return[1] contains every Nth letter starting from the second
 * return[factor-1] contains every Nth letter starting from the Nth letter
 */
std::vector<string> getSubStrings(const string& message, unsigned factor) {
    // Initialize vector with empty strings
    std::vector<string> substrings(factor, std::string(""));
    for(auto& item: substrings){
        // Reserve approximate space for each string
        item.reserve(message.size()/factor);
    }
    // Iterate over all letters
    for(int i=0;i<message.size();i++){
        // Put each letter in the appropriate substring.
        substrings.at(i % factor).push_back(message[i]);
    }
    return substrings;
}

std::vector<std::pair<char,unsigned >> letterFrequencies(const string &message) {
    // Latin alphabet - 26 letters
    auto counts = std::vector<std::pair<char,unsigned >>();
    for(char c='A'; c <= 'Z'; c++) {
        counts.emplace_back(c, 0);
    }
    // Measure appearances of each character
    for(auto character: message){
        counts.at(character - 'A').second++;
    }
    return counts;
}

std::string calculateKey(const std::vector<string> &substrings) {
    // Code adapted from: http://rosettacode.org/wiki/Vigen%C3%A8re_cipher/Cryptanalysis#C.2B.2B
    // All subkeys must be of equal length of course
    // Calculate letter frequencies for each substring
    std::vector<std::vector<std::pair<char,unsigned>>> frequencies;
    for(const auto& substring: substrings) {
        auto substringFrequences = letterFrequencies(substring);
        std::sort(substringFrequences.begin(), substringFrequences.end(),
                  [](std::pair<char,unsigned> first, std::pair<char,unsigned> second){
                        return first.second > second.second;
        });
        frequencies.push_back(substringFrequences);
    }
    auto key = std::string();
    // Check all the substrings
    for(size_t i=0; i<substrings.size(); i++){
        auto bestShift = 0;
        auto bestShiftCorr = 0.0;
        // Check all shifts
        for(size_t j=0;j<26;j++) {
            auto currentCorr = 0.0;
            auto c = 'A' + j;
            // Check all letters of the subkey
            for(size_t k=0;k<26;k++) {
                // Calculate shifted letter index
                // frequencies[i][k].first - c = 'A'+ letter index in alphabet - 'A' - shift value
                // Results can be in range [-25,25], so add 26 and mod 26 to bring them in range [0,25]
                unsigned d = (frequencies[i][k].first - c +26)%26;
                // Multiply frequency of original letter by the expected english frequency of the transformed letter
                currentCorr += frequencies[i][k].second * english_frequencies[d];
            }
            if(currentCorr > bestShiftCorr) {
                bestShiftCorr = currentCorr;
                bestShift = c;
            }
        }
        key.push_back(bestShift);
    }
    return key;
}

int main(){
    // Remove newlines
    text.erase(std::remove(text.begin(), text.end(), '\n'), text.end());
    auto distances = findRepeats(text,3);
    auto factorCounts = getFactorCount(distances);
    // Factor counts are stored in Number,Count pairs
    // Use custom compare to order by count. Priority queue puts larger items at the top.
    auto comparePairs = [] (pair first, pair second){
        return first.second < second.second;
    };
    // Create a priority queue from the map
    std::priority_queue<pair,std::vector<pair>, decltype(comparePairs)> q(factorCounts.cbegin(), factorCounts.cend(),comparePairs);
    // Get all substrings (caesar ciphers) using key length of the factor that appeared the most times.
    auto substrings = getSubStrings(text,q.top().first);

    std::cout << calculateKey(substrings);
    return 0;
}