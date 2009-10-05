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

public class HomerBoot {
		private String h1="     ,;   .r3&952;            / __)(  _ \\  /__\\  (  _ \\(  _ \\( \\/ )(  _ \\(  _ \\";                                                      
		private String h2="    ##5rh@@GSrrrs5A@#2       ( (__  )   / /(__)\\  )___/ )___/ \\  /  )(_) )) _ <";                                                        
		private String h3="   @i  5@9,,::::::,:r&@Hr .   \\___)(_)\\_)(__)(__)(__)  (__)   (__) (____/(____/";     
		private String h4=" .#@;5@B,,::::::::::::SMA#@&GBA";                                                                        
		private String h5="s@s@,@r,:::::::::::::::.X@2:. .rH@:               is booting now...";                                       
		private String h6="@  r@&:,::;:::::::,:s5i@.       @ r@";                                                          
		private String h7="A@ @r:;:::::::::;::A#9r:rX#h     .;#@   rr";                                                     
		private String h8=" B@@:;ss;:::;::;:;@r       &@  s@#S;M@2@M#@.";                                                   
		private String h9="  .@Mrrsir;:::::,@:         i@@#;...;@M::,@H";                                                   
		private String h10="   .@r;rrsisr::,,@   :@,     @..;h@G:.::.3#";                                                    
		private String h11="    s@2;;rrsisr,.5@,  .    ,@;.3#h;.,:,rM@                          i2i";                 
		private String h12="      @@S;;rrssr;.r@Ar,,;5#Br2#X:.,,;ih2#M                        :@&2M@.";               
		private String h13="       ;#@X;rrrr;;:,r9A#B3;2B5,..:sXG9@ii                        .@...X@2s";            
		private String h14="         ,A@r;@MAG5r;:,,..#h. ;5X2&@i2,                      ,:.  H#.:.B@:X";           
		private String h15="            r&X@2i9irsr,.@S.i2iX@A#2                       .@###HB#M.:,.@#&";            
		private String h16="            ,iH@@ri2X;rsX#ri9Ai@@@i;                       .@h;.@A .:::.,@";             
		private String h17="            G@, .@G9Ai;;@3@r@@@@@@2A                         ;X@# ,::,.;@&AAX,";       
		private String h18="              &;@;9@rrG:@X@@@@@@@@@@#5                         @:.::::,,,ris5@;";      
		private String h19="                r@S59@A,@s@@@@@@hi92@@;rr                     @9.:;;::::,,;r2@,";      
		private String h20="r92               ::M@,,SA:@@@@3:2@@@@#i@@:r                :@3 ::;:;rr;GM2r;";        
		private String h21="@iG@;                G#,,92:#@@X;rG@@A:3@B@@MG.            ,A@;.::::;isr@9";            
		private String h22="@..r@                 @A,,X9:rA#&Ahs. .@@ .2 ;#9         iM@S.,:::::ss;@r";             
		private String h23="@5..MH                 @r:,;9Xsrsrrs@B#@:9#@ @@XhiSr;h@,.;X##&r.,:;:;ss;";              
		private String h24="A:..;@                9#:;,.;h@@5, 5S ,S2@@;#@;:@@AA2;,,,:::;:::ss:#9";               
		private String h25=" .2GH@2                @;r2h9;      #        ;3S @;.,,::::::::;sr;#&";                
		private String h26="S@A;,,5@s              @MXs;.       #r        r@G,@.,::;:;:::;srr@2";                 
		private String h27="@@..;X;s@             B3   .,:.   ,5#r         i@2 @i,::;::::rs;3@,";                  
		private String h28=".SHAB3.&H      .5iXhG3@h;:..,:i&##G:             9@i@.::::::;rrr#9";                    
		private String h29="  .,..:@#3r:. 2@@X     ,rhMA993s,                 ,@A,@.::rsrrA@:";                     
		private String h30=",:,::::,,SA#M#A@r2@B        B@                     h@: @,;rssrsA#";                       
		private String h31=":::::::,:,,,:::.,.,@M        .@;                        &ASA@B&SrHBs";                         
		private String h32=";:;:::::::::::,,,,,;@;         @.                             rA#@M.";                           
		private String h33=";:::::::::::::::::,;A@         AA                                r9r";                           
		private String h34="@s::::::::::;:::::::rs@         #h                                 ;#H";                         
		private String h35="r@#r;;:::::::::;::::r;@,   .,::;@                                    r@,";                       
		private String h36=" 2@MSrr;;:::::::::rr;@X,;rrr::@;                               .       5r";             
		private String h37="   s##Girrr;;;;;;rsr:@3,;;,:2@r                                        @@M";          
		private String h38="     .iM#A35irrrr;;;,@s .:X#G:                                           @B";       
		private String h39="         :i&H#MBHH329@ ;&#h, .                                           M@@";      
		private String h40="               .,,@@h@#&s. .:,                                          hGAAH@"; 
		private String h41="===============================================================================";
		
		protected Logger logger = Logger.getLogger(HomerBoot.class);
		
		public void splashScreen() {
			logger.info(h1);
			logger.info(h2);
			logger.info(h3);
			logger.info(h4);
			logger.info(h5);
			logger.info(h6);
			logger.info(h7);
			logger.info(h8);
			logger.info(h9);
			logger.info(h10);
			
			logger.info(h11);
			logger.info(h12);
			logger.info(h13);
			logger.info(h14);
			logger.info(h15);
			logger.info(h16);
			logger.info(h17);
			logger.info(h18);
			logger.info(h19);
			logger.info(h20);
			
			logger.info(h21);
			logger.info(h22);
			logger.info(h23);
			logger.info(h24);
			logger.info(h25);
			logger.info(h26);
			logger.info(h27);
			logger.info(h28);
			logger.info(h29);
			
			logger.info(h30);
			logger.info(h31);
			logger.info(h32);
			logger.info(h33);
			logger.info(h34);
			logger.info(h35);
			logger.info(h36);
			logger.info(h37);
			logger.info(h38);
			logger.info(h39);
			logger.info(h40);
			
			logger.info(h41);
		}
}
