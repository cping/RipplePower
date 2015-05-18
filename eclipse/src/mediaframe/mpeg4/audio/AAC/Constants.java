/**
 * MediaFrame is an Open Source streaming media platform in Java 
 * which provides a fast, easy to implement and extremely small applet 
 * that enables to view your audio/video content without having 
 * to rely on external player applications or bulky plug-ins.
 * 
 * Copyright (C) 2004/5 MediaFrame (http://www.mediaframe.org).
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
/************************** MPEG-2 NBC Audio Decoder **************************
 *                                                                           
 * "This software module was originally developed by 
 * AT&T, Dolby Laboratories, Fraunhofer Gesellschaft IIS in the course of 
 * development of the MPEG-2 NBC/MPEG-4 Audio standard ISO/IEC 13818-7, 
 * 14496-1,2 and 3. This software module is an implementation of a part of one or more 
 * MPEG-2 NBC/MPEG-4 Audio tools as specified by the MPEG-2 NBC/MPEG-4 
 * Audio standard. ISO/IEC  gives users of the MPEG-2 NBC/MPEG-4 Audio 
 * standards free license to this software module or modifications thereof for use in 
 * hardware or software products claiming conformance to the MPEG-2 NBC/MPEG-4
 * Audio  standards. Those intending to use this software module in hardware or 
 * software products are advised that this use may infringe existing patents. 
 * The original developer of this software module and his/her company, the subsequent 
 * editors and their companies, and ISO/IEC have no liability for use of this software 
 * module or modifications thereof in an implementation. Copyright is not released for 
 * non MPEG-2 NBC/MPEG-4 Audio conforming products.The original developer
 * retains full right to use the code for his/her  own purpose, assign or donate the 
 * code to a third party and to inhibit third party from using the code for non 
 * MPEG-2 NBC/MPEG-4 Audio conforming products. This copyright notice must
 * be included in all copies or derivative works." 
 * Copyright(c)1996.
 * 
 ******************************************************************************/
package mediaframe.mpeg4.audio.AAC;

/**
 * Constants
 */
interface Constants {

	public static final boolean DOLBY_MDCT = true;

	/*
	 * interface between the encoder and decoder
	 */

	public static final float C_LN10 = 2.30258509299404568402f; /* ln(10) */
	public static final float C_PI = 3.14159265358979323846f; /* pi */
	public static final float C_SQRT2 = 1.41421356237309504880f; /* sqrt(2) */

	public static final float MINTHR = 0.5f;
	public static final float SF_C1 = (float) (13.33333 / 1.333333);

	/* prediction */
	public static final int PRED_ORDER = 2;
	public static final float PRED_ALPHA = 0.9f;
	public static final float PRED_A = 0.95f;
	public static final float PRED_B = 0.95f;
	/*
	 * // ifndef OLD_PRED_PARAMS public static final float PRED_ALPHA = 0.90625;
	 * public static final float PRED_A = 0.953125; public static final float
	 * PRED_B = 0.953125;
	 */

	/*
	 * block switching
	 */
	public static final int LN = 2048;
	public static final int SN = 256;
	public static final int LN2 = LN / 2;
	public static final int SN2 = SN / 2;
	public static final int LN4 = LN / 4;
	public static final int SN4 = SN / 4;
	public static final int NSHORT = LN / SN;
	public static final int MAX_SBK = NSHORT;
	public static final int ONLY_LONG_WINDOW = 0;
	public static final int LONG_START_WINDOW = 1; // TODO verify these
													// constants
	public static final int EIGHT_SHORT_WINDOW = 2;
	public static final int LONG_STOP_WINDOW = 3;
	public static final int NUM_WIN_SEQ = 4;
	public static final int WLONG = 0;
	public static final int WSTART = 1;
	public static final int WSHORT = 2;
	public static final int WSTOP = 3;
	public static final int MAXBANDS = 16 * NSHORT; /*
													 * max number of scale
													 * factor bands
													 */
	public static final int MAXFAC = 121; /* maximum scale factor */
	public static final int MIDFAC = (MAXFAC - 1) / 2;
	public static final int SF_OFFSET = 100; /* global gain must be positive */

	/** specify huffman tables as signed (true) or unsigned (false) */
	public static final boolean HUF1SGN = true;
	public static final boolean HUF2SGN = true;
	public static final boolean HUF3SGN = false;
	public static final boolean HUF4SGN = false;
	public static final boolean HUF5SGN = true;
	public static final boolean HUF6SGN = true;
	public static final boolean HUF7SGN = false;
	public static final boolean HUF8SGN = false;
	public static final boolean HUF9SGN = false;
	public static final boolean HUF10SGN = false;
	public static final boolean HUF11SGN = false;

	public static final int BY4BOOKS = 4;
	public static final int ESCBOOK = 11;
	public static final int NSPECBOOKS = ESCBOOK + 1;
	public static final int BOOKSCL = NSPECBOOKS;
	public static final int NBOOKS = NSPECBOOKS + 1;

	public static final int INTENSITY_HCB = 15;
	public static final int INTENSITY_HCB2 = 14;

	public static final int LONG_SECT_BITS = 5;
	public static final int SHORT_SECT_BITS = 3;

	/*
	 * Program Configuration
	 */
	public static final int Main_Profile = 0;
	public static final int LC_Profile = 1;
	public static final int SRS_Profile = 3;

	public static final int Fs_48 = 3;
	public static final int Fs_44 = 4;
	public static final int Fs_32 = 5;

	/*
	 * Misc constants
	 */
	public static final int CC_DOM = 0; /* before TNS */
	public static final int CC_IND = 1;

	/*
	 * Raw bitstream constants
	 */
	public static final int LEN_SE_ID = 3;
	public static final int LEN_TAG = 4;
	public static final int LEN_ICS_RESERV = 1;
	public static final int LEN_WIN_SEQ = 2;
	public static final int LEN_WIN_SH = 1;
	public static final int LEN_MAX_SFBL = 6;
	public static final int LEN_MAX_SFBS = 4;
	public static final int LEN_CB = 4;
	public static final int LEN_SCL_PCM = 8;
	public static final int LEN_PRED_PRES = 1;
	public static final int LEN_PRED_RST = 1;
	public static final int LEN_PRED_RSTGRP = 5;
	public static final int LEN_PRED_ENAB = 1;
	public static final int LEN_MASK_PRES = 2;
	public static final int LEN_MASK = 1;

	public static final int LEN_NEC_NPULSE = 2;
	public static final int LEN_NEC_ST_SFB = 6;
	public static final int LEN_NEC_POFF = 5;
	public static final int LEN_NEC_PAMP = 4;
	public static final int NUM_NEC_LINES = 4;
	public static final int NEC_OFFSET_AMP = 4;
	public static final int LEN_NCC = 3;
	public static final int LEN_IS_CPE = 1;
	public static final int LEN_CC_LR = 1;
	public static final int LEN_CC_DOM = 1;
	public static final int LEN_CC_SGN = 1;
	public static final int LEN_CCH_GES = 2;
	public static final int LEN_CCH_CGP = 1;
	public static final int LEN_D_CNT = 4;
	public static final int LEN_D_ESC = 12;
	public static final int LEN_F_CNT = 4;
	public static final int LEN_F_ESC = 8;
	public static final int LEN_BYTE = 8;
	public static final int LEN_PAD_DATA = 8;

	public static final int LEN_PC_COMM = 8;

	/* sfb 40, coef 672, pred bw of 15.75 kHz */
	public static final int MAX_PRED_SFB = 40;

	public static final int ID_SCE = 0;
	public static final int ID_CPE = 1; // TODO verify these constants
	public static final int ID_CCE = 2;
	public static final int ID_LFE = 3;
	public static final int ID_DSE = 4;
	public static final int ID_PCE = 5;
	public static final int ID_FIL = 6;
	public static final int ID_END = 7;

	/* PLL's don't like idle channels! */
	public static final int FILL_VALUE = 0x55;

	/*
	 * program configuration element
	 */
	public static final int LEN_PROFILE = 2;
	public static final int LEN_SAMP_IDX = 4;
	public static final int LEN_NUM_ELE = 4;
	public static final int LEN_NUM_LFE = 2;
	public static final int LEN_NUM_DAT = 3;
	public static final int LEN_NUM_CCE = 4;
	public static final int LEN_MIX_PRES = 1;
	public static final int LEN_ELE_IS_CPE = 1;
	public static final int LEN_IND_SW_CCE = 1;
	public static final int LEN_COMMENT_BYTES = 8;

	/*
	 * audio data interchange format header
	 */
	public static final int LEN_ADIF_ID = (32 / 8);
	public static final int LEN_COPYRT_PRES = 1;
	public static final int LEN_COPYRT_ID = (72 / 8);
	public static final int LEN_ORIG = 1;
	public static final int LEN_HOME = 1;
	public static final int LEN_BS_TYPE = 1;
	public static final int LEN_BIT_RATE = 23;
	public static final int LEN_NUM_PCE = 4;
	public static final int LEN_ADIF_BF = 20;

	//
	// channels for 5.1 main profile configuration
	// (modify for any desired decoder configuration)
	//
	/*
	 * public static final int FChans = 3; // front channels: left, center,
	 * right public static final int FCenter = 1; // 1 if decoder has front
	 * center channel public static final int SChans = 0; // side channels:
	 * public static final int BChans = 2; // back channels: left surround,
	 * right surround public static final int BCenter = 0; // 1 if decoder has
	 * back center channel public static final int LChans = 1; // LFE channels
	 * public static final int XChans = 1; // scratch space for parsing unused
	 * channels
	 * 
	 * public static final int ICChans = 1; // independently switched coupling
	 * channels public static final int DCChans = 2; // dependently switched
	 * coupling channels public static final int XCChans = 1; // scratch space
	 * for parsing unused coupling channels
	 */
	public static final int FChans = 2; // front channels: left, center, right
	public static final int FCenter = 0; // 1 if decoder has front center
											// channel
	public static final int SChans = 0; // side channels:
	public static final int BChans = 0; // back channels: left surround, right
										// surround
	public static final int BCenter = 0; // 1 if decoder has back center channel
	public static final int LChans = 0; // LFE channels
	public static final int XChans = 0; // scratch space for parsing unused
										// channels

	public static final int ICChans = 0; // independently switched coupling
											// channels
	public static final int DCChans = 0; // dependently switched coupling
											// channels
	public static final int XCChans = 0; // scratch space for parsing unused
											// coupling channels

	public static final int Chans = FChans + SChans + BChans + LChans + XChans;
	public static final int CChans = ICChans + DCChans + XCChans;

	/* block switch windows for single channels or channel pairs */
	public static final int Winds = Chans;

	/* average channel block length, bytes */
	public static final int Avjframe = 341;

	public static final int TEXP = 128; /* size of exp cache table */
	public static final int MAX_IQ_TBL = 128; /* size of inv quant table */
	public static final int MAXFFT = LN4;

	public static final int nil = 0;
	public static final int Tnleaf = 0x8000;

	public static final int TNS_MAX_BANDS = 51; // 49;
	public static final int TNS_MAX_ORDER = 31;
	public static final int TNS_MAX_WIN = 8;
	public static final int TNS_MAX_FILT = 3;

	public static final int MAX_PGRAD = 2;
	public static final int MINVAR = 1;
	public static final float FLT_MIN = 1.17549435E-38f;

	public static final int LEFT_CHAN = 0;
	public static final int RIGHT_CHAN = 1;
	public static final int CENTER_CHAN = 2;
	public static final int LFE_CHAN = 3;
	public static final int LEFT_SURR_CHAN = 4;
	public static final int RIGHT_SURR_CHAN = 5;
	public static final int MAX_CHANNELS = 6;

	public static final int LONG_BLOCK = 0;
	public static final int START_BLOCK = 1;
	public static final int SHORT_BLOCK = 2;
	public static final int STOP_BLOCK = 3;
	public static final int AUTO_SWITCH = 4;/*
											 * AT&T: filter bank detects
											 * switching inside
											 */

	/* Required definitions for time to frequency mapping module: */

	/* definitions of the spectral resolutions of the windows */
	public static final int SHORT_BLOCKS_IN_LONG_BLOCK = 8; /*
															 * no of short
															 * blocks replacing
															 * one long block
															 */
	public static final int BLOCK_LEN_LONG = 1024; /*
													 * #spectral values in long
													 * blocks
													 */
	public static final int BLOCK_LEN_SHORT = 128; /*
													 * #spectral values in short
													 * blocks
													 */
	public static final int BLOCK_LEN_START = 1024; /*
													 * #spectral values in start
													 * blocks
													 */
	public static final int BLOCK_LEN_STOP = 1024; /*
													 * #spectral values in stop
													 * blocks
													 */
	public static final int N_SHORT_IN_START = 4; /* GAD 10/30/95 */
	public static final int N_SHORT_IN_STOP = 3;

	/* definitions of the frequency output buffer sizes */
	public static final int OUTPUT_LEN_LONG = BLOCK_LEN_LONG;
	public static final int OUTPUT_LEN_SHORT = BLOCK_LEN_LONG;
	public static final int OUTPUT_LEN_START = BLOCK_LEN_LONG;
	public static final int OUTPUT_LEN_STOP = BLOCK_LEN_LONG;
	public static final int MAX_OUTPUT_LEN = BLOCK_LEN_LONG;

	public static final int DEBUG_P = 0;
	public static final int DEBUG_V = 1;
	public static final int DEBUG_G = 2;
	public static final int DEBUG_M = 3;
	public static final int DEBUG_F = 4;
	public static final int DEBUG_S = 5;
	public static final int DEBUG_Q = 6;
	public static final int DEBUG_I = 7;
	public static final int DEBUG_C = 8;
	public static final int DEBUG_R = 9;
	public static final int DEBUG_T = 10;
	public static final int DEBUG_N = 11;
	public static final int DEBUG_X = 12;

	public static final char[] debug_options = { 'P', 'V', 'G', 'M', 'F', 'S',
			'Q', 'I', 'C', 'R', 'T', 'N', 'X' };
}
