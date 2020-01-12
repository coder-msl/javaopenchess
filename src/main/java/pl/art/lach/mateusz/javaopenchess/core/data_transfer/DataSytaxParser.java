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
package pl.art.lach.mateusz.javaopenchess.core.data_transfer;

import java.util.List;

import pl.art.lach.mateusz.javaopenchess.core.data_transfer.tokenizers.Token;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.DataSyntaxException;

/**
 * Interface to implement data import syntax validator from different notations.
 * 
 * @author Mateusz Slawomir Lach (matlak, msl)
 */
public interface DataSytaxParser {

    List<Token> parse(String input) throws DataSyntaxException;
    
}
