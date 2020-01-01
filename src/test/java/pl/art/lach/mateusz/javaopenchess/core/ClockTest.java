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
package pl.art.lach.mateusz.javaopenchess.core;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.art.lach.mateusz.javaopenchess.core.PlayerClock;

/**
 * @author Mateusz Slawomir Lach (matlak, msl)
 */
public class ClockTest {

    @Test
    public void testPositiveDecrementation() {
        PlayerClock clock = PlayerClock.getInstanceWithTimeLeft(50);
        boolean result = clock.decrementOneSecond();
        assertEquals(49, clock.getLeftTime());
        assertTrue(result);
    }
    @Test
    public void testNegativeDecrementation() {
        PlayerClock clock = PlayerClock.getInstanceWithTimeLeft(0);
        boolean result = clock.decrementOneSecond();
        assertEquals(0, clock.getLeftTime());
        assertFalse(result);
    }
    
    @Test
    public void testgetAsStringOneHour() {
        PlayerClock clock = PlayerClock.getInstanceWithTimeLeft(60);
        String result = clock.getAsString();
        assertEquals("01:00", result);
    }

    @Test
    public void testgetAsStringOneHourAndFiveMinutes() {
        PlayerClock clock = PlayerClock.getInstanceWithTimeLeft(65);
        String result = clock.getAsString();
        assertEquals("01:05", result);
    }
    
    @Test
    public void testgetAsStringOneHourAndTenMinutes() {
        PlayerClock clock = PlayerClock.getInstanceWithTimeLeft(70);
        String result = clock.getAsString();
        assertEquals("01:10", result);
    }
    
    @Test
    public void testgetAsStringElevenMinutes() {
        PlayerClock clock = PlayerClock.getInstanceWithTimeLeft(660);
        String result = clock.getAsString();
        assertEquals("11:00", result);
    }
    
    @Test
    public void testClockDefaultValueSetsToZero() {
        PlayerClock clock = PlayerClock.getZeroTimeInstance();
        int time = clock.getLeftTime();
        
        assertEquals(0, time);
        assertEquals("00:00", clock.getAsString());
    }
}
