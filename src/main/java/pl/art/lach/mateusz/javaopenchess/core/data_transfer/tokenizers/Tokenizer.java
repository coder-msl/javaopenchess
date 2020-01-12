/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.art.lach.mateusz.javaopenchess.core.data_transfer.tokenizers;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.art.lach.mateusz.javaopenchess.core.exceptions.DataSyntaxException;

/**
 * @author Mateusz Slawomir Lach (matlak, msl)
 */
public class Tokenizer {
    
    private static final String REGEXP_BEGINNING_OF_STR = "^";

    private List<TokenMatcher> tokenInfos = new LinkedList<>();
    
    public void add(String regexp, int tokenNumber) {
        Pattern pattern = Pattern.compile(getRegExpMatchingBeggining(regexp));
        TokenMatcher info = new TokenMatcher(pattern, tokenNumber);
        tokenInfos.add(info);
    }
    
    public String getRegExpMatchingBeggining(final String regexp) {
        String result = regexp;
        if (!result.startsWith(REGEXP_BEGINNING_OF_STR)) {
            result = REGEXP_BEGINNING_OF_STR + regexp;
        }
        return result;
    }
    
    public List<Token> parse(final String input) throws DataSyntaxException {
        List<Token> result = new LinkedList<>();
        String remainedInput = input;
        for (TokenMatcher tokenInfo : tokenInfos) {
            Matcher matcher = tokenInfo.getRegexpPattern().matcher(remainedInput);
            if (matcher.find()) {
                String token = matcher.group().trim();
                result.add(new Token(token, tokenInfo.getTokenNumber()));
                remainedInput = matcher.replaceFirst("").trim();
            } else {
                String at = remainedInput.substring(0, 10);
                throw new DataSyntaxException("Exception during parse operation at: " + at + "(...)");
            }
        }
        return result;
    }

}
