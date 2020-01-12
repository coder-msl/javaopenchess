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

import java.util.regex.Pattern;

/**
 * @author Mateusz Slawomir Lach (matlak, msl)
 */
class TokenMatcher {
    
    private final Pattern regexpPattern;
    
    private final int tokenNumber;
    
    public TokenMatcher(final Pattern regexpPattern, final int tokenNumber) {
        this.regexpPattern = regexpPattern;
        this.tokenNumber = tokenNumber;
    }
    
    public Pattern getRegexpPattern() {
        return regexpPattern;
    }
    
    public int getTokenNumber() {
        return tokenNumber;
    }
    
}