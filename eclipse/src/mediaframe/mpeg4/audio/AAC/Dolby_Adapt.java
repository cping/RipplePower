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

import java.io.IOException;

/**
 * Dolby_Adapt
 */
/* source dolby_adapt.c */
public final class Dolby_Adapt {

	final static int NORM_TYPE = 0;
	final static int START_TYPE = 1;
	final static int SHORT_TYPE = 2;
	final static int STOP_TYPE = 3;

	final static int N_SHORT_IN_START = 4;
	final static int START_OFFSET = 0;
	final static int SHORT_IN_START_OFFSET = 5;
	final static int N_SHORT_IN_STOP	= 3;
	final static int STOP_OFFSET	= 3;
	final static int SHORT_IN_STOP_OFFSET	= 0;
	final static int N_SHORT_IN_4STOP	= 4;

	final static int BLOCK_LEN_LONG	 = 1024;
	final static int BLOCK_LEN_SHORT = 128;


	final static int NWINLONG	= BLOCK_LEN_LONG;
	final static int ALFALONG	= 4;
	final static int NWINSHORT	= BLOCK_LEN_SHORT;
	final static int ALFASHORT	= 7;

	/** flat params */
	final static int NWINFLAT	= NWINLONG;					
	/** Advanced flat params */
	final static int NWINADV	= NWINLONG-NWINSHORT;		
	final static int NFLAT		= (NWINFLAT-NWINSHORT)/2;
	final static int NADV0		= (NWINADV-NWINSHORT)/2;

	
	final static int WS_FHG = 0; 
	final static int WS_DOLBY = 1; 
	final static int N_WINDOW_SHAPES = 2; 

	final static int WT_LONG = 0; 
	final static int WT_SHORT = 1; 
	final static int WT_FLAT = 2; 
	final static int WT_ADV = 3; 
	final static int N_WINDOW_TYPES = 4; 

	/** ADVanced transform types */
	final static int LONG_BLOCK = 0; 
	final static int START_BLOCK = 1; 
	final static int SHORT_BLOCK = 2; 
	final static int STOP_BLOCK = 3; 
	final static int START_ADV_BLOCK = 4; 
	final static int STOP_ADV_BLOCK = 5; 
	final static int START_FLAT_BLOCK = 6; 
	final static int STOP_FLAT_BLOCK = 7; 
	final static int N_BLOCK_TYPES = 8; 

	/** Advanced window sequence (frame) types */
	final static int ONLY_LONG = 0; 
	final static int LONG_START = 1; 
	final static int LONG_STOP = 2; 
	final static int SHORT_START = 3; 
	final static int SHORT_STOP = 4; 
	final static int EIGHT_SHORT = 5; 
	final static int SHORT_EXT_STOP = 6; 
	final static int NINE_SHORT = 7; 
	final static int OLD_START = 8; 
	final static int OLD_STOP = 9; 
	final static int N_WINDOW_SEQUENCES = 10; 

	private boolean	dolbyShortOffset = true;
	private float[]	transBuff  = new float[2*BLOCK_LEN_LONG];
	private float[]	timeOut = new float[BLOCK_LEN_LONG];

	private float[]	fhg_long = new float[NWINLONG];
	private float[]	fhg_short = new float[NWINSHORT];
	private float[]	fhg_edler = new float[NWINLONG];
	private float[]	dol_edler = new float[NWINLONG];
	private float[]	fhg_adv = new float[NWINADV];
	private float[]	dol_adv = new float[NWINADV];
/*	
	private float[][][]	windowPtr = {
		{Tables.dol_long,	Tables.dol_long},
		{fhg_short,	fhg_short},
		{fhg_edler,	fhg_edler},
		{fhg_adv,	fhg_adv}
	};	
*/	
	private float[][][]	windowPtr = {
		{fhg_long,	Tables.dol_long},
		{fhg_short,	fhg_short},
		{fhg_edler,	fhg_edler},
		{fhg_adv,	fhg_adv}
	};	
	

	private int[]	windowLeng = {
		NWINLONG,	
		NWINSHORT,	
		NWINLONG,	
		NWINADV
	};

	/*
	*	Interleave Definitions for start and stop blocks
	*
	*	Start block contains 1 576-pt spectrum (A) and 4 128-pt spectra (B-E)
	*	  Input spectra are interleaved in repeating segements of 17 bins,
	*		9 bins from A (A0-A8), and 2 bins from each of the shorts.
	*	  Within the segments the bins are interleaved as:
	*		A0 A1 A2 A3 A4 B0 C0 D0 E0 A5 A6 A7 A8 B1 C1 D1 E1
	*
	*	Stop block contains 3 128-pt spectra (A-C) and 1 576-pt spectrum (D)
	*	  Input spectra are interleaved in repeating segements of 15 bins,
	*		2 bins from each of the shorts, and 9 bins from D (D0-D8).
	*	  Within the segments the bins are interleaved as:
	*		A0 B0 C0 D0 D1 D2 D3 D4 A1 B1 C1 D5 D6 D7 D8
	*	  The last 64 bins are (should be) set to 0.
	*/

	/*****************************************************************************
	*
	*	freq2time_adapt
	*	transform freq. domain data to time domain.  
	*	Overlap and add transform output to recreate time sequence.
	*	Blocks composed of multiple segments (i.e. all but long) have 
	*	  input spectrums interleaved.
	*	input: see below
	*	output: see below
	*	local static:
	*	  timeBuff		time domain data fifo
	*	globals: none
	*
	*****************************************************************************/
	void freq2time_adapt (

		byte blockType,			/* input: blockType 0-3						*/
		Wnd_Shape wnd_shape, 	/* input/output								*/
		float[] freqIn, 		/* input: interleaved spectrum				*/
		float[] timeBuff, 		/* transform state needed for each channel	*/
		float[] ftimeOut)		/* output: 1/2 block of new time values		*/
		throws IOException 
	{
		int		transBuffPtr = 0, timeBuffPtr = 0, destPtr = 0, srcPtr = 0;
		int		i, j;

//		windShape = wnd_shape.this_bk;		/*	window_shape [ch];	*/

//		System.out.println("blockType = " + blockType);

		/*	mapping from old FhG blocktypes to new window sequence types */
		switch (blockType)
		{
			case NORM_TYPE:
				blockType = ONLY_LONG;
				break;
			case START_TYPE:
				blockType = OLD_START;
				break;
			case SHORT_TYPE:
				blockType = EIGHT_SHORT;
				break;
			case STOP_TYPE:
				blockType = OLD_STOP;
				break;
			default:
				throw new IOException("dolby_adapt.c: Illegal block type " + blockType + " - aborting");
		}

		if (blockType == ONLY_LONG)  {
			unfold (freqIn, srcPtr, transBuff, 1, BLOCK_LEN_LONG);
			/* Do 1 LONG transform */
			ITransformBlock (transBuff, LONG_BLOCK, wnd_shape, timeBuff);	/* ch ); */
	/* Add first half and old data */
			transBuffPtr = 0;
			timeBuffPtr = 0;		/*	 [ch];	*/
			destPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeOut[destPtr++] = transBuff[transBuffPtr++] + timeBuff[timeBuffPtr++];
			}
	/* Save second half as old data */
			timeBuffPtr = 0;		/*		 [ch];		*/
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
			}
		}

		else if (blockType == SHORT_START)  {
			/* Do 1 START, 4 SHORT transforms */

			unfold (freqIn, srcPtr, transBuff, 1, (BLOCK_LEN_SHORT + BLOCK_LEN_LONG) / 2);

			ITransformBlock (transBuff, START_BLOCK, wnd_shape, timeBuff);
			/* Add first half and old data */
			transBuffPtr = 0;
			timeBuffPtr = 0;	/*	 [ch];	*/
			destPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeOut[destPtr++] = transBuff[transBuffPtr++] + timeBuff[timeBuffPtr++];
			}
			/* Save second half as old data */
			timeBuffPtr = 0;		/*		 [ch];		*/
			for (i = 0; i < BLOCK_LEN_SHORT; i++)  {
				timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
			}
			srcPtr = ((BLOCK_LEN_LONG + BLOCK_LEN_SHORT) / 2); /* SHORT_IN_START_OFFSET;	*/
			timeBuffPtr = 0;   /*	 [ch];		*/
			for (i = 0; i < N_SHORT_IN_START; i++)  {
				unfold (freqIn, srcPtr, transBuff, 1, BLOCK_LEN_SHORT);
				srcPtr += BLOCK_LEN_SHORT;

				ITransformBlock (transBuff, SHORT_BLOCK, wnd_shape, timeBuff);
				/* Add first half of short window and old data */
				transBuffPtr = 0;
				for (j = 0; j < BLOCK_LEN_SHORT; j++)  {
					timeBuff[timeBuffPtr++] += transBuff[transBuffPtr++];
				}
				/* Save second half of short window */
				for (j = 0; j < BLOCK_LEN_SHORT; j++)  {
					timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
				}
				timeBuffPtr -= BLOCK_LEN_SHORT;		/* go back for overlap add */
			}
			dolbyShortOffset = true;
			
		} else if (blockType == EIGHT_SHORT)  {
			/* Do 8 SHORT transforms */

			if (dolbyShortOffset)
				destPtr = 0 + 4 * BLOCK_LEN_SHORT;		/* DBS */
			else
				destPtr = 0 + (BLOCK_LEN_LONG - BLOCK_LEN_SHORT) / 2;	/*	448	*/

			for (i = 0; i < 8; i++) {
				unfold (freqIn, srcPtr, transBuff, 1, BLOCK_LEN_SHORT );
				/*was freqinPtr++, 8 .. mfd */
				srcPtr += BLOCK_LEN_SHORT;   /*  added mfd   */
				ITransformBlock (transBuff, SHORT_BLOCK, wnd_shape, timeBuff);

				/* Add first half of short window and old data */
				transBuffPtr = 0;
				for (j = 0; j < BLOCK_LEN_SHORT; j++)  {
					timeBuff[destPtr++] += transBuff[transBuffPtr++];
				}
				/* Save second half of short window */
				for (j = 0; j < BLOCK_LEN_SHORT; j++)  {
					timeBuff[destPtr++] = transBuff[transBuffPtr++];
				}
				destPtr -= BLOCK_LEN_SHORT;
			}
			/* Copy data to output buffer */
			destPtr = 0;
			timeBuffPtr = 0;		/*		 [ch];		*/
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeOut[destPtr++] = timeBuff[timeBuffPtr++];
			}
			/* Update timeBuff fifo */
			destPtr = 0;		/*		 [ch];		*/
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeBuff[destPtr++] = timeBuff[timeBuffPtr++];
			}

		} else if (blockType == SHORT_STOP)  {
			/* Do 3 SHORT, 1 STOP transforms */
			destPtr = 4 * BLOCK_LEN_SHORT;
			srcPtr = 0; /*  + SHORT_IN_STOP_OFFSET;	*/
			for (i = 0; i < N_SHORT_IN_STOP; i++)  {
				unfold (freqIn, srcPtr, transBuff, 1, BLOCK_LEN_SHORT );
				srcPtr += BLOCK_LEN_SHORT;
				ITransformBlock (transBuff, SHORT_BLOCK, wnd_shape, timeBuff);
				/* Add first half of short window and old data */
				transBuffPtr = 0;
				for (j = 0; j < BLOCK_LEN_SHORT; j++ )  {
					timeBuff[destPtr++] += transBuff[transBuffPtr++];
				}
				/* Save second half of short window */
				for (j = 0; j<BLOCK_LEN_SHORT; j++ )  {
					timeBuff[destPtr++] = transBuff[transBuffPtr++];
				}
				destPtr -= BLOCK_LEN_SHORT;
			}	/*	i loop */

			unfold (freqIn, srcPtr, transBuff, 1, (BLOCK_LEN_SHORT + BLOCK_LEN_LONG) / 2);
			ITransformBlock( transBuff, STOP_BLOCK, wnd_shape, timeBuff);
			/* Add first half of short window and old data */
			transBuffPtr = 0;
			for( i=0; i < BLOCK_LEN_SHORT; i++ )  {
				timeBuff[destPtr++] += transBuff[transBuffPtr++];
			}
			/* Copy new data to output buffer and update timeBuff fifo */
			destPtr = 0;
			timeBuffPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++ )  {
				timeOut[destPtr++] = timeBuff[timeBuffPtr];
				timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
			}

		} else if (blockType == LONG_START)  {
			unfold(freqIn, srcPtr, transBuff, 1, 960);

			ITransformBlock (transBuff, START_ADV_BLOCK, wnd_shape, timeBuff);

			/* Add first half and old data */
			transBuffPtr = 0;
			timeBuffPtr = 0;
			destPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeOut[destPtr++] = transBuff[transBuffPtr++] + timeBuff[timeBuffPtr++];
			}
			/* Save second half as old data */
			timeBuffPtr = 0;	/*	NWINADV = 896	*/
			for (i = 0; i < NWINADV; i++)  { /* mab changed to i<512  from i < NWINADV*/
				timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
			}
			for ( ; i < (2*(BLOCK_LEN_LONG)); i++)  { /* mab changed to i<960 from i<BLOCK_LEN_LONG */
				timeBuff[timeBuffPtr++] = 0;
			}

		} else if (blockType == LONG_STOP)  {
			unfold (freqIn, srcPtr, transBuff, 1, 960);
			/* Do 1 LONG transforms */
			ITransformBlock (transBuff, STOP_ADV_BLOCK, wnd_shape, timeBuff);
			/* Add first half and old data */
			transBuffPtr = 0;
			timeBuffPtr = 0;
			destPtr = 0;
			for (i = 0 ; i < (BLOCK_LEN_LONG - 896); i++)  {
				timeOut[destPtr++] = timeBuff[timeBuffPtr++];
			}
			for ( ; i < BLOCK_LEN_LONG; i++)  {
				timeOut[destPtr++] = transBuff[transBuffPtr++] + timeBuff[timeBuffPtr++];
			}
			/* Save second half as old data */
			timeBuffPtr = 0;
			for ( ; i < (2*(BLOCK_LEN_LONG)); i++ )  {
				timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
			}

		} else if (blockType == SHORT_EXT_STOP)  {
			/* Do 4 SHORT, 1 STOP transforms */
			destPtr = 3 * BLOCK_LEN_SHORT;
			for (i = 0; i < 4; i++)  {
				unfold (freqIn, srcPtr, transBuff, 1, BLOCK_LEN_SHORT);
				srcPtr += BLOCK_LEN_SHORT;   /*  added mfd   */
				ITransformBlock (transBuff, SHORT_BLOCK, wnd_shape, timeBuff);
				/* Add first half of short window and old data */
				transBuffPtr = 0;
				for (j = 0; j < BLOCK_LEN_SHORT; j++ )  {
					timeBuff[destPtr++] += transBuff[transBuffPtr++];
				}
				/* Save second half of short window */
				for( j=0; j<BLOCK_LEN_SHORT; j++ )  {
					timeBuff[destPtr++] = transBuff[transBuffPtr++];
				}
				destPtr -= BLOCK_LEN_SHORT;
			}
			unfold (freqIn, srcPtr, transBuff, 1, (BLOCK_LEN_SHORT+BLOCK_LEN_LONG)/2);
			ITransformBlock (transBuff, STOP_BLOCK, wnd_shape, timeBuff);
			/* Add first half of short window and old data */
			transBuffPtr = 0;
			for( i=0; i < BLOCK_LEN_SHORT; i++ )  {
				timeBuff[destPtr++] += transBuff[transBuffPtr++];
			}
			/* Copy new data to output buffer and update timeBuff fifo */
			destPtr = 0;
			timeBuffPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++ )  {
				timeOut[destPtr++] = timeBuff[timeBuffPtr];
				timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
			}
		} else if (blockType == NINE_SHORT)  {
			/* Do 9 SHORT transforms */
			destPtr = 3 * BLOCK_LEN_SHORT;

			for (i = 0; i < 9; i++) {
				unfold( freqIn, srcPtr, transBuff, 1, BLOCK_LEN_SHORT );  /* !!! needs to be adjusted for "cramming" */
				srcPtr += BLOCK_LEN_SHORT;   /*  added mfd   */
				ITransformBlock (transBuff, SHORT_BLOCK, wnd_shape, timeBuff);

				/* Add first half of short window and old data */
				transBuffPtr = 0;
				for (j = 0; j < BLOCK_LEN_SHORT; j++)  {
					timeBuff[destPtr++] += transBuff[transBuffPtr++];
				}
				/* Save second half of short window */
				for (j = 0; j < BLOCK_LEN_SHORT; j++)  {
					timeBuff[destPtr++] = transBuff[transBuffPtr++];
				}
				destPtr -= BLOCK_LEN_SHORT;
			}
			/* Copy data to output buffer */
			destPtr = 0;
			timeBuffPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeOut[destPtr++] = timeBuff[timeBuffPtr++];
			}
			/* Update timeBuff fifo */
			destPtr = 0;
			for ( ; i < (2*(BLOCK_LEN_LONG)); i++)  {
				timeBuff[destPtr++] = timeBuff[timeBuffPtr++];
			}
			dolbyShortOffset = true;
		} else if (blockType == OLD_START)  {
			unfold(freqIn, srcPtr, transBuff, 1, BLOCK_LEN_LONG);
			ITransformBlock (transBuff, START_FLAT_BLOCK, wnd_shape, timeBuff);
			/* Add first half and old data */
			transBuffPtr = 0;
			timeBuffPtr = 0;
			destPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeOut[destPtr++] = transBuff[transBuffPtr++] + timeBuff[timeBuffPtr++];
			}
			/* Save second half as old data */
			timeBuffPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++)  {
				timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
			}
			dolbyShortOffset = false;
		} else if (blockType == OLD_STOP)  {
			unfold (freqIn, srcPtr, transBuff, 1, BLOCK_LEN_LONG);
			/* Do 1 LONG transforms */
			ITransformBlock (transBuff, STOP_FLAT_BLOCK, wnd_shape, timeBuff);
			/* Add first half and old data */
			transBuffPtr = 0;
			timeBuffPtr = 0;
			destPtr = 0;
			for (i = 0; i < (BLOCK_LEN_LONG - NFLAT); i++)  {
				timeOut[destPtr++] = transBuff[transBuffPtr++] + timeBuff[timeBuffPtr++];
			}
			for ( ; i < BLOCK_LEN_LONG; i++)  {
				timeOut[destPtr++] = transBuff[transBuffPtr++];
			}
			/* Save second half as old data */
			timeBuffPtr = 0;
			for (i = 0; i < BLOCK_LEN_LONG; i++ )  {
				timeBuff[timeBuffPtr++] = transBuff[transBuffPtr++];
			}
		} else {
			throw new IOException("Illegal Block_type " + blockType + " in time2freq_adapt(), aborting ...");
		}

		for (i = 0; i < BLOCK_LEN_LONG; i++)  {
			ftimeOut [i] = timeOut [i];
			/*		ftimeOutPtr [i] = 1.;	*/
		}
	}
	
	/* source block.c */
	/*****************************************************************************
	*
	*	InitBlock
	*	calculate windows for use by Window()
	*	input: none
	*	output: none
	*	local static: none
	*	globals: shortWindow[], longWindow[]
	*
	*****************************************************************************/
	void InitBlock () {

		/* calc half-window data */
		int     i, j;
		double	phaseInc;

	/* init half-windows */

	/* FhG long window */
		phaseInc = (Math.PI / (2.0f * (float)NWINLONG));
		for (i = 0; i < NWINLONG; i++) {
			fhg_long [i]  = (float)Math.sin (phaseInc * ((float) i + 0.5f));
		}

	/* FhG short window */
		phaseInc = Math.PI / (2.0f * (float)NWINSHORT);
		for (i = 0; i < NWINSHORT; i++) {
			fhg_short [i] = (float)Math.sin (phaseInc * ((float) i + 0.5f));
		}

	/* Edler windows */
		for (i = 0, j = 0; i < NFLAT; i++, j++) {
			fhg_edler[j] = 0;
			dol_edler[j] = 0;
		}
		for (i = 0; i < NWINSHORT; i++, j++) {
			fhg_edler [j] = fhg_short [i];
			dol_edler [j] = Tables.dol_short [i];
		}
		for ( ; j < NWINFLAT; j++) {
			fhg_edler [j] = 1;
			dol_edler [j] = 1;
		}

	/* Advanced Edler windows */
		for (i = 0, j = 0; i < NADV0; i++, j++) {
			fhg_adv [j] = 0;
			dol_adv [j] = 0;
		}
		for (i = 0; i < NWINSHORT; i++, j++) {
			fhg_adv[j] = fhg_short[i];
			dol_adv[j] = Tables.dol_short[i];
		}
		for ( ; j < NWINADV; j++) {
			fhg_adv[j] = 1;
			dol_adv[j] = 1;
		}
	}


	/*****************************************************************************
	*
	*	Window
	*	window input sequence based on window type
	*	input: see below
	*	output: see below
	*	local static:
	*	  firstTime				flag = need to initialize data structures
	*	globals: shortWindow[], longWindow[]
	*
	*****************************************************************************/

	private boolean firstTime = true;

	void ITransformBlock (
		float[] dataPtr,			/* vector to be windowed in place	*/
		int bT,						/* input: window type				*/
		Wnd_Shape wnd_shape,
		float[] state				/* input/output						*/
		) {
		int			leng0, leng1;
		int			i,leng;
		float[]		windPtr;
		int			beginWT, endWT;
		int			dataPtr_index = 0;
		int			winPtr_index = 0;

		if (firstTime)  {
			InitBlock();			/*	calculate windows	*/
			firstTime = false;
		}

		if((bT==LONG_BLOCK) || (bT==START_BLOCK) || (bT==START_FLAT_BLOCK)
			|| (bT==START_ADV_BLOCK))  {
			beginWT = WT_LONG;
		} else if(bT==STOP_FLAT_BLOCK) {
			beginWT = WT_FLAT;
		} else if(bT==STOP_ADV_BLOCK) {
			beginWT = WT_ADV;
		} else {	
			beginWT = WT_SHORT;
		}

		if ((bT == LONG_BLOCK) || (bT == STOP_BLOCK) || (bT == STOP_FLAT_BLOCK)
			|| (bT == STOP_ADV_BLOCK)) {
			endWT = WT_LONG;
		} else if (bT == START_FLAT_BLOCK)  {
			endWT = WT_FLAT;
		} else if (bT == START_ADV_BLOCK)  {
			endWT = WT_ADV;
		} else {	
			endWT = WT_SHORT;
		}

		leng0 = windowLeng [beginWT];
		leng1 = windowLeng [endWT];

		MDCT.ITransform (dataPtr, leng0 + leng1, leng1);

		/*	first half of window */
		windPtr = windowPtr [beginWT] [wnd_shape.prev_bk]; 


		for (i = 0; i < windowLeng [beginWT]; i++)  {
			dataPtr[dataPtr_index++] *= windPtr[winPtr_index++];
		}

	/*	second half of window */
		leng = windowLeng [endWT];
		windPtr = windowPtr [endWT] [wnd_shape.this_bk];
		winPtr_index = leng - 1;
		for (i = 0; i < leng; i++) {
			dataPtr[dataPtr_index++] *= windPtr[winPtr_index--];
		}

		wnd_shape.prev_bk = wnd_shape.this_bk;
	}

	/*****************************************************************************
	*
	*	unfold
	*	create full spectrum by reflecting-inverting first half over to second
	*	input: see below 
	*	output: see below
	*	local static: none
	*	globals: none
	*
	*****************************************************************************/
	void unfold ( 
		float[] data_in,	/* input: 1/2 spectrum */
		int data_in_ptr,
		float[] data_out,	/* output: full spectrum */
		int inStep,			/* input: input array increment */
		int inLeng)			/* input: length of input vector */
	{
		int   i;
		/* int            step, j;
		   double		  *srcPtr, *destPtr, *mirrorPtr; unused - SRQ */

		/* fill transBuff w/ full MDCT sequence from freqInPtr */
		for (i=0;i<inLeng;i++) {
			data_out[i] = data_in[data_in_ptr];
			data_out[2*inLeng-i-1] = -(data_in[data_in_ptr]);
			data_in_ptr += inStep;
		}
	} /* end of unfold */

}
