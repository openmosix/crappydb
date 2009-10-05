/*
 *  This file is part of CrappyDB-Server, 
 *  developed by Luca Bonmassar <luca.bonmassar at gmail.com>
 *
 *  CrappyDB-Server is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  CrappyDB-Server is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CrappyDB-Server.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bonmassar.crappydb.server;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestHomerBoot {

	private HomerBoot boot;
	
	@Before
	public void setUp() {
		boot = new HomerBoot();
		boot.logger = mock(Logger.class);
	}
	
	//Just to please coverage :D
	@Test
	public void testSplashScreen() {
		boot.splashScreen();
		verify(boot.logger).info("     ,;   .r3&952;            / __)(  _ \\  /__\\  (  _ \\(  _ \\( \\/ )(  _ \\(  _ \\");
		verify(boot.logger).info("    ##5rh@@GSrrrs5A@#2       ( (__  )   / /(__)\\  )___/ )___/ \\  /  )(_) )) _ <");		
		verify(boot.logger).info("   @i  5@9,,::::::,:r&@Hr .   \\___)(_)\\_)(__)(__)(__)  (__)   (__) (____/(____/");
		verify(boot.logger).info(" .#@;5@B,,::::::::::::SMA#@&GBA");
		verify(boot.logger).info("s@s@,@r,:::::::::::::::.X@2:. .rH@:               is booting now...");
		verify(boot.logger).info("@  r@&:,::;:::::::,:s5i@.       @ r@");
		verify(boot.logger).info("A@ @r:;:::::::::;::A#9r:rX#h     .;#@   rr");
		verify(boot.logger).info(" B@@:;ss;:::;::;:;@r       &@  s@#S;M@2@M#@.");
		verify(boot.logger).info("  .@Mrrsir;:::::,@:         i@@#;...;@M::,@H");
		verify(boot.logger).info("   .@r;rrsisr::,,@   :@,     @..;h@G:.::.3#");
		verify(boot.logger).info("    s@2;;rrsisr,.5@,  .    ,@;.3#h;.,:,rM@                          i2i");
		verify(boot.logger).info("      @@S;;rrssr;.r@Ar,,;5#Br2#X:.,,;ih2#M                        :@&2M@.");
		verify(boot.logger).info("       ;#@X;rrrr;;:,r9A#B3;2B5,..:sXG9@ii                        .@...X@2s");
		verify(boot.logger).info("         ,A@r;@MAG5r;:,,..#h. ;5X2&@i2,                      ,:.  H#.:.B@:X");
		verify(boot.logger).info("            r&X@2i9irsr,.@S.i2iX@A#2                       .@###HB#M.:,.@#&");
		verify(boot.logger).info("            ,iH@@ri2X;rsX#ri9Ai@@@i;                       .@h;.@A .:::.,@");
		verify(boot.logger).info("            G@, .@G9Ai;;@3@r@@@@@@2A                         ;X@# ,::,.;@&AAX,");
		verify(boot.logger).info("              &;@;9@rrG:@X@@@@@@@@@@#5                         @:.::::,,,ris5@;");
		verify(boot.logger).info("                r@S59@A,@s@@@@@@hi92@@;rr                     @9.:;;::::,,;r2@,");
		verify(boot.logger).info("r92               ::M@,,SA:@@@@3:2@@@@#i@@:r                :@3 ::;:;rr;GM2r;");
		verify(boot.logger).info("@iG@;                G#,,92:#@@X;rG@@A:3@B@@MG.            ,A@;.::::;isr@9");
		verify(boot.logger).info("@..r@                 @A,,X9:rA#&Ahs. .@@ .2 ;#9         iM@S.,:::::ss;@r");
		verify(boot.logger).info("@5..MH                 @r:,;9Xsrsrrs@B#@:9#@ @@XhiSr;h@,.;X##&r.,:;:;ss;");
		verify(boot.logger).info("A:..;@                9#:;,.;h@@5, 5S ,S2@@;#@;:@@AA2;,,,:::;:::ss:#9");
		verify(boot.logger).info(" .2GH@2                @;r2h9;      #        ;3S @;.,,::::::::;sr;#&");
		verify(boot.logger).info("S@A;,,5@s              @MXs;.       #r        r@G,@.,::;:;:::;srr@2");
		verify(boot.logger).info("@@..;X;s@             B3   .,:.   ,5#r         i@2 @i,::;::::rs;3@,");
		verify(boot.logger).info(".SHAB3.&H      .5iXhG3@h;:..,:i&##G:             9@i@.::::::;rrr#9");
		verify(boot.logger).info("  .,..:@#3r:. 2@@X     ,rhMA993s,                 ,@A,@.::rsrrA@:");
		verify(boot.logger).info(",:,::::,,SA#M#A@r2@B        B@                     h@: @,;rssrsA#");
		verify(boot.logger).info(":::::::,:,,,:::.,.,@M        .@;                        &ASA@B&SrHBs");
		verify(boot.logger).info(";:;:::::::::::,,,,,;@;         @.                             rA#@M.");
		verify(boot.logger).info(";:::::::::::::::::,;A@         AA                                r9r");
		verify(boot.logger).info("@s::::::::::;:::::::rs@         #h                                 ;#H");
		verify(boot.logger).info("r@#r;;:::::::::;::::r;@,   .,::;@                                    r@,");
		verify(boot.logger).info(" 2@MSrr;;:::::::::rr;@X,;rrr::@;                               .       5r");
		verify(boot.logger).info("   s##Girrr;;;;;;rsr:@3,;;,:2@r                                        @@M");
		verify(boot.logger).info("     .iM#A35irrrr;;;,@s .:X#G:                                           @B");
		verify(boot.logger).info("         :i&H#MBHH329@ ;&#h, .                                           M@@");
		verify(boot.logger).info("               .,,@@h@#&s. .:,                                          hGAAH@");
		verify(boot.logger).info("===============================================================================");
	}
}
