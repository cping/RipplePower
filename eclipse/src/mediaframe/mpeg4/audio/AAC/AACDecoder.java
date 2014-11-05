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
 * AACDecoder
 * 
 */
public final class AACDecoder {
	
//	static final boolean[] debug = new boolean[20];
	
	Tns tns = null;
	Config config = null;
	Huffman huffman = null;
	BitStream audio_stream = null;
	
	byte[]  sect = new byte[2*(Constants.MAXBANDS+1)];
	short[] factors[] = new short[2][Constants.MAXBANDS];

	Info[]	win_seq_info = new Info[Constants.NUM_WIN_SEQ];
	Info[]  winmap = new Info[Constants.NUM_WIN_SEQ];
	Info	only_long_info = new Info();
	Info	eight_short_info = new Info();
	short[]		sfbwidth128 = new short[(1<<Constants.LEN_MAX_SFBS)];
	int maxfac = Constants.TEXP;

	Nec_Info 	nec_info = new Nec_Info();
	MC_Info		mc_info = new MC_Info();
	ADIF_Header	adif_header;
	ProgConfig	prog_config = new ProgConfig();

	private int nsect;
	private int global_gain;

	long	bno = 0;
	boolean default_config;
	int		current_program;

	float[][] coef = null;
	float[][] data = null;
	float[][] state = null; /*	changed LN4 to LN 1/97 mfd	*/
	byte[][] group = null;
	int[][] lpflag = null;
	int[][] prstflag = null;
	TNS_frame_info[] tns_frame_info = null;
	PRED_STATUS[][] sp_status = null;
	float[][] prev_quant = null;
	byte[][] mask = null;
	byte[] hasmask = null;
	byte[] wnd = null;
	byte[] max_sfb = null;
	Wnd_Shape[] wnd_shape = null;
	byte[] d_bytes = null;

/*
	coupling data - skipped
	float[][] cc_coef = null;
	float[][][] cc_gain = null;
	float[][] cc_data = null; 
	float[][] cc_state = null;
	byte[] cc_wnd = null;
	Wnd_Shape[] cc_wnd_shape = null;
*/	 

	
	Dolby_Adapt dolby_Adapt = new Dolby_Adapt();

/*	
	int		nbits;
	long		cword;
	int		inerror;
	int		bufpool;
	Float		iq_exp_tbl[MAX_IQ_TBL];
	Float		exptable[TEXP];
	int		maxfac;
	Hcb		book[NSPECBOOKS+2];
	Stab		table[MAXFFT/2];
	Stab		table2[MAXFFT];
	int		adif_header_present;
	MC_Info		mc_info;
	int		debug[256];
	Info		*win_seq_info[NUM_WIN_SEQ];
	Info		only_long_info;
	Info		eight_short_info;
	Info*		winmap[NUM_WIN_SEQ];
	short		sfbwidth128[(1<<LEN_MAX_SFBS)];
*/	

	public AACDecoder(BitStream audio_stream) throws IOException {
		super();
		this.audio_stream = audio_stream;
		this.huffman = new Huffman(this); 
		this.config = new Config(this);
		this.tns = new Tns(this); 

		int i;
		int j;
/*
		for(i = 0; i < debug.length; i++) {
			debug[i] = false;
		}
*/
		coef = new float[Constants.Chans][Constants.LN2];
		data = new float[Constants.Chans][Constants.LN2];
		state = new float[Constants.Chans][Constants.LN]; /*	changed LN4 to LN 1/97 mfd	*/
		group = new byte[Constants.Chans][Constants.NSHORT];
		lpflag = new int[Constants.Chans][Constants.MAXBANDS];
		prstflag = new int[Constants.Chans][Constants.LEN_PRED_RSTGRP+1];
		tns_frame_info = new TNS_frame_info[Constants.Chans];
		for(i = 0; i < tns_frame_info.length; i ++) {
			tns_frame_info[i] = new TNS_frame_info();
		}
		sp_status = new PRED_STATUS[Constants.Chans][Constants.LN2];
		for(i = 0; i < sp_status.length; i ++) {
			for(j = 0; j < sp_status[i].length; j ++) {
				sp_status[i][j] = new PRED_STATUS();
			}
		}
		prev_quant = new float[Constants.Chans][Constants.LN2];
		mask = new byte[Constants.Winds][Constants.MAXBANDS];
		hasmask = new byte[Constants.Winds];
	
		wnd = new byte[Constants.Chans];
		max_sfb = new byte[Constants.Chans];
		wnd_shape = new Wnd_Shape[Constants.Chans];
		for(i = 0; i < wnd_shape.length; i ++) {
			wnd_shape[i] = new Wnd_Shape();
		}
		d_bytes = new byte[Constants.Avjframe];

		for(i = 0; i < Constants.Chans; i++){
			for (j = 0; j < Constants.LN; j++) {
				state[i][j] = 0;
			}
		}


/*		
		for(i = 0; i < Constants.NUM_WIN_SEQ; i++){
			win_seq_info[i] = new Info();
		}

		if(Constants.CChans > 0) {
			cc_coef = new float[Constants.CChans][Constants.LN2];
			cc_gain = new float[Constants.CChans][Constants.Chans][Constants.MAXBANDS];
			cc_wnd = new byte[Constants.CChans];
			cc_wnd_shape = new Wnd_Shape[Constants.CChans];
			if(Constants.ICChans > 0) {
				cc_data = new float[Constants.CChans][Constants.LN2]; 
				cc_state = new float[Constants.CChans][Constants.LN4];
			} 5
		}
*/

		infoinit(Tables.samp_rate_info[mc_info.sampling_rate_idx]);

		/* set defaults */
		adif_header_present = false;
		current_program = -1;
		default_config = true;
//		mc_info.profile = Constants.Main_Profile;
//		mc_info.sampling_rate_idx = Constants.Fs_48;

		init();
		
	}
	
	/* source huffinit.c */	
	void infoinit(SR_Info sip) { 
		int i, j, k, n, ws;
		short[] sfbands;
		/* long block info */
		Info ip = only_long_info;

		win_seq_info[Constants.ONLY_LONG_WINDOW] = ip;
		ip.islong = true;
		ip.nsbk = 1;
		ip.bins_per_bk = Constants.LN2;
		for (i=0; i < ip.nsbk; i++) {
			ip.sfb_per_sbk[i] = sip.nsfb1024;
			ip.sectbits[i] = Constants.LONG_SECT_BITS;
			ip.sbk_sfb_top[i] = sip.SFbands1024;
		}
		ip.sfb_width_128 = null;
		ip.num_groups = 1;
		ip.group_len[0] = 1;
		ip.group_offs[0] = 0;
    
		/* short block info */
		ip = eight_short_info;
		win_seq_info[Constants.EIGHT_SHORT_WINDOW] = ip;
		ip.islong = false;
		ip.nsbk = Constants.NSHORT;
		ip.bins_per_bk = Constants.LN2;
		for (i=0; i<ip.nsbk; i++) {
			ip.sfb_per_sbk[i] = sip.nsfb128;
			ip.sectbits[i] = Constants.SHORT_SECT_BITS;
			ip.sbk_sfb_top[i] = sip.SFbands128;
		}
		/* construct sfb width table */
		ip.sfb_width_128 = sfbwidth128;
		for (i=0, j=0, n=sip.nsfb128; i<n; i++) {
			k = sip.SFbands128[i];
			sfbwidth128[i] = (short)(k - j);
			j = k;
		}
    
		/* common to long and short */
		for (ws=0; ws<Constants.NUM_WIN_SEQ; ws++) {
			if ((ip = win_seq_info[ws]) == null)
				continue;
			ip.sfb_per_bk = 0;   
			k = 0;
			n = 0;
			for (i=0; i<ip.nsbk; i++) {
				/* compute bins_per_sbk */
				ip.bins_per_sbk[i] = ip.bins_per_bk / ip.nsbk;
	    	
				/* compute sfb_per_bk */
				ip.sfb_per_bk += ip.sfb_per_sbk[i];

				/* construct default (non-interleaved) bk_sfb_top[] */
				sfbands = ip.sbk_sfb_top[i];
				for (j=0; j < ip.sfb_per_sbk[i]; j++)
					ip.bk_sfb_top[j+k] = (short)(sfbands[j] + n);

				n += ip.bins_per_sbk[i];
				k += ip.sfb_per_sbk[i];
			}	    
/*			if (debug[Constants.DEBUG_I]) {
				System.out.println("\nsampling rate " + sip.samp_rate);
				System.out.println("win_info\t" +  ws + " has " + ip.nsbk + " windows");
				System.out.println("\tbins_per_bk\t" + ip.bins_per_bk);
				System.out.println("\tsfb_per_bk\t" + ip.sfb_per_bk);
				for (i=0; i<ip.nsbk; i++) {
					System.out.println("window\t" + i);
					System.out.println("\tbins_per_sbk\t" + ip.bins_per_sbk[i]);
					System.out.println("\tsfb_per_sbk	" + ip.sfb_per_sbk[i]);
				}
				if (ip.sfb_width_128 != null) {
					System.out.println("sfb top and width");
					for (i=0; i<ip.sfb_per_sbk[0]; i++) {
						System.out.println(i + " " + ip.sbk_sfb_top[0][i] + " " +  
						ip.sfb_width_128[i]);
					}
				}		   
			}
*/			
		}
	}

	/**
	 * Read and decode the data for the next 1024 output samples
	 * return -1 if there was an error.
	 * @throws IOException raises if an I/O error occurs.
	 */
	int	huffdecode(int id, MC_Info mip, byte[] win, Wnd_Shape[] wshape, 
		byte[][] group, byte[] hasmask, byte[][] mask, byte[] max_sfb, 
		int[][] lpflag, int[][] prstflag, TNS_frame_info[] tns, float[][] coef) throws IOException {
			
		int i, tag, ch, widx, first = 0, last = 0;
		boolean common_window;

		Info info = new Info();

		tag = (int)audio_stream.next_bits(Constants.LEN_TAG);

		switch(id) {
		case Constants.ID_SCE:
		case Constants.ID_LFE:
			common_window = false;
			break;
		case Constants.ID_CPE:
			common_window = audio_stream.next_bit();
			break;
		default:
			throw new IOException("Unknown id " + id);
		}

		if ((ch = config.chn_config(id, tag, common_window, mip)) < 0)
			return -1;
    
		switch(id) {
		case Constants.ID_SCE:
		case Constants.ID_LFE:
			widx = mip.ch_info[ch].widx;
			first = ch;
			last = ch;
			hasmask[widx] = 0;
			break;
		case Constants.ID_CPE:
			first = ch;
			last = mip.ch_info[ch].paired_ch;
			if (common_window) {
				widx = mip.ch_info[ch].widx;
				get_ics_info(widx, win, wshape, group[widx],
					max_sfb, lpflag[widx], prstflag[widx]);
				hasmask[widx] = (byte)getmask(winmap[win[widx]], group[widx], 
					max_sfb[widx], mask[widx]);
			} else { 
				hasmask[mip.ch_info[first].widx] = 0;
				hasmask[mip.ch_info[last].widx] = 0;
			}
			break;
		}
/*		
		if(debug[Constants.DEBUG_V]) {
			System.out.println("tag " + tag + ", common window " + common_window);
			System.out.println("nch " + (last-first+1) + ", channels " + first + " " + last + ", widx " + mip.ch_info[first].widx + " " + mip.ch_info[last].widx);
		}
*/
		for (i=first; i<=last; i++) {
			widx = mip.ch_info[i].widx;
			for(int j = 0; j < Constants.LN2; j++) {
				coef[i][j] = 0;
			}
			if(getics(widx, info, common_window, win, wshape,
				group[widx], max_sfb, lpflag[widx], prstflag[widx], 
				sect, coef[i], factors[i-first], tns[i]) == 0)
					return -1;
		}

		/* identify intensity sections */
		if ((id == Constants.ID_CPE) && common_window) {
			/* sectioning info is now that of right channel */
			int j, k, bot, top, table, is_cb, is_sfb, is_sect;
			IS_Info iip = mip.ch_info[last].is_info;

			bot=0;
			is_sfb=0;
			is_sect=0;
			for (i=0, j=0; i<nsect; i++) {
				table=sect[j];
				top=sect[j+1];
				/* is IS used?
			 	* (but no IS if factor[left] is zero!)
		 		*/
				is_cb = ((table == Constants.INTENSITY_HCB) ||
				(table == Constants.INTENSITY_HCB2)) ? 1 : 0;
				if (is_cb > 0) {
/*					if (debug[Constants.DEBUG_I]) {
						System.out.println("intensity cb " + table + " from " + bot + " to " + top);
					} */	
					iip.is_present = true;
					iip.bot[is_sect] = bot;
					iip.top[is_sect] = top;
					iip.sign[is_sect] = 
						(table == Constants.INTENSITY_HCB) ? 1 : -1;
					is_sect++;
				}
					for (k=bot; k<top; k++) {
					if (is_cb > 0) {
						/* [0] is left, [1] is right */
						iip.fac[is_sfb] = factors[1][is_sfb];
/*						if (debug[Constants.DEBUG_I])
							System.out.println("IS factor " + iip.fac[is_sfb] + " at sfb " + is_sfb); */
					}
					is_sfb++;
				}
				bot = top;
				j+=2;
			}
			iip.n_is_sect = is_sect;
		}
		return 0;
	}
	
	void get_ics_info(int widx, byte[] win, Wnd_Shape[] wshape, byte[] group,
			byte[] max_sfb, int[] lpflag, int[] prstflag) throws IOException {
		int i, j;
		Info info;
//		System.out.println("get_ics_info()");

		audio_stream.next_bits(Constants.LEN_ICS_RESERV);	    /* reserved bit */
		win[widx] = (byte)audio_stream.next_bits(Constants.LEN_WIN_SEQ);
		wshape[widx].this_bk = (byte)audio_stream.next_bits(Constants.LEN_WIN_SH);
		if ((info = winmap[win[widx]]) == null)
			throw new IOException("bad window code");

		/*
		 * max scale factor, scale factor grouping and prediction flags
		 */
		prstflag[0] = 0;
		if (info.islong) {
			max_sfb[widx] = (byte)audio_stream.next_bits(Constants.LEN_MAX_SFBL);
			group[0] = 1;
			if ((lpflag[0] = (int)audio_stream.next_bits(Constants.LEN_PRED_PRES)) > 0) {
				if ((prstflag[0] = (int)audio_stream.next_bits(Constants.LEN_PRED_RST)) > 0) {
					for(i=1; i<Constants.LEN_PRED_RSTGRP+1; i++) {
						prstflag[i] = (int)audio_stream.next_bits(Constants.LEN_PRED_RST);
					}
				}
				j = ( (max_sfb[widx] < Constants.MAX_PRED_SFB) ? 
					max_sfb[widx] : Constants.MAX_PRED_SFB ) + 1;
				for (i = 1; i < j; i++)
					lpflag[i] = (int)audio_stream.next_bits(Constants.LEN_PRED_ENAB);
				for ( ; i < Constants.MAX_PRED_SFB+1; i++)
					lpflag[i] = /* 0 */ 1 /* SRQ FIX!!!! */;
			}
		} else {
			max_sfb[widx] = (byte)audio_stream.next_bits(Constants.LEN_MAX_SFBS);
			getgroup(info, group);
			lpflag[0] = 0;
		}
/*		if(debug[Constants.DEBUG_V]) {
			System.out.println("window_sequence " + win[widx] + ", window_shape " + wshape[widx].this_bk);
			System.out.println("max_sf " + max_sfb[widx]);
		} */

/*		if (debug[Constants.DEBUG_P]) {
			if (lpflag[0] > 0) {
				System.out.print("prediction enabled (" + max_sfb[widx] + "):  ");
				for (i = 1; i < Constants.MAX_PRED_SFB+1; i++)
					System.out.print(" " + lpflag[i]);
				System.out.println();
			}
		}
*/		
	}
	
	/*********************************************************************/

	static void	deinterleave(int inptr[], int outptr[], int ngroups,
		short nsubgroups[], int ncells[], short cellsize[]) {
		int i, j, k, l, intptr_index, outptr_index;
		int start_inptr, start_subgroup_ptr, subgroup_ptr;
		short cell_inc, subgroup_inc;

		outptr_index = intptr_index = start_subgroup_ptr = 0;

		for (i = 0; i < ngroups; i++)
		{
			cell_inc = 0;
			start_inptr = intptr_index;

			/* Compute the increment size for the subgroup pointer */
			subgroup_inc = 0;
			for (j = 0; j < ncells[i]; j++) {
				subgroup_inc += cellsize[j];
			}
	
			/* Perform the deinterleaving across all subgroups in a group */
			for (j = 0; j < ncells[i]; j++) {
				subgroup_ptr = start_subgroup_ptr;

				for (k = 0; k < nsubgroups[i]; k++) {
					outptr_index = subgroup_ptr + cell_inc;
					for (l = 0; l < cellsize[j]; l++) {
						outptr[outptr_index++] = inptr[intptr_index++];
					}
					subgroup_ptr += subgroup_inc;
				}
				cell_inc += cellsize[j];
			}
			start_subgroup_ptr += (intptr_index - start_inptr);
		}
	}

	static void	calc_gsfb_table(Info info, byte[] group) {
		int group_offset;
		int group_idx;
		int offset;
		short group_offset_p;
		int sfb,len;
		/* first calc the group length*/
		if (info.islong){
			return;
		} else {
			group_offset = 0;
			group_idx = 0;
			do  {
				info.group_len[group_idx]=(short)(group[group_idx] - group_offset);
				group_offset=group[group_idx];
				group_idx++;
			} while (group_offset < 8);
			info.num_groups=group_idx;
			group_offset_p = 0;
			offset=0;
			for (group_idx = 0;group_idx < info.num_groups;group_idx++){
				len = info.group_len[group_idx];
				for (sfb = 0;sfb < info.sfb_per_sbk[group_idx];sfb++){
					offset += info.sfb_width_128[sfb] * len;
					info.bk_sfb_top[group_offset_p++] = (short)offset;
				}
			}
		}
	}

	// checked
	void getgroup(Info info, byte[] group) throws IOException {
		int i, group_index = 0;
		boolean first_short = true;

/*		if( debug[Constants.DEBUG_G] ) 
			System.out.print("Grouping: 0"); */
			     
		for (i = 0; i < info.nsbk; i++) {
			if (info.bins_per_sbk[i] > Constants.SN2) {
				/* non-short windows are always their own group */
				group[group_index++] = (byte)(i+1);
			} else {
				/* only short-window sequences are grouped! */
				if (first_short) {
					/* first short window is always a new group */
					first_short = false;
				} else {
					if(((int)audio_stream.next_bits(1)) == 0) {
						group[group_index++] = (byte)i;
					}
/*					if( debug[Constants.DEBUG_G] ) 
						System.out.print(j); */  
				}
			}
		}
		group[group_index] = (byte)i;
/*		if( debug[Constants.DEBUG_G] ) 
			System.out.println(); */
	}

	/*
	 * read a synthesis mask
	 *  uses EXTENDED_MS_MASK
	 *  and grouped mask 
	 */
	int
	getmask(Info info, byte[] group, byte max_sfb, byte[] mask) throws IOException {
		int b, i, mp;
		int group_index = 0, mask_index = 0;

		mp = (int)audio_stream.next_bits(Constants.LEN_MASK_PRES);
/*		if( debug[Constants.DEBUG_M] )
			System.out.println("Ext. Mask Present: " + mp); */  

		/* special EXTENDED_MS_MASK cases */
		if(mp == 0) { /* no ms at all */
			return 0;
		}
		if(mp == 2) {/* MS for whole spectrum on, mask bits set to 1 */
			for(b = 0; b < info.nsbk; b = group[group_index++])
				for(i = 0; i < info.sfb_per_sbk[b]; i ++)
					mask[mask_index++] = 1;
			return 0;
		}

		/* otherwise get mask */
		for(b = 0; b < info.nsbk; b = group[group_index++]){
/*			if( debug[Constants.DEBUG_M] ) 
				System.out.print(" gr" + b + ":");        */
			for(i = 0; i < max_sfb; i ++) {
				mask[mask_index] = (byte)audio_stream.next_bits(Constants.LEN_MASK);
/*				if( debug[Constants.DEBUG_M] )
					System.out.print(mask[mask_index]);		*/
				mask_index++;
			}
			for( ; i < info.sfb_per_sbk[b]; i++){
				mask[mask_index] = 0;
/*				if( debug[Constants.DEBUG_M] ) 
					System.out.print(mask[mask_index]);		*/
				mask_index++;
			}
		}
/*		
		if( debug[Constants.DEBUG_M] ) 
			System.out.println();
*/			
		return 1;
	}

	void clr_tns( Info info, TNS_frame_info tns_frame_info )
	{
		int s;

		tns_frame_info.n_subblocks = info.nsbk;
		for (s = 0; s < tns_frame_info.n_subblocks; s++) {
			tns_frame_info.info[s].n_filt = 0;
		}
	}

	static final int neg_mask[] = {0xfffc, 0xfff8, 0xfff0};
	static final int sgn_mask[] = {0x2, 0x4, 0x8};

	int get_tns( Info info, TNS_frame_info tns_frame_info ) throws IOException {
		int                       f, t, top, res, res2, compress;
		int                       s;
		int                       sp, tmp, s_mask, n_mask;
		TNSfilt[]                 tns_filt;
		TNSinfo                   tns_info;
		boolean short_flag = !info.islong;
		int tns_filt_index = 0;

		short_flag = (!info.islong);
		tns_frame_info.n_subblocks = info.nsbk;

		for (s = 0; s < tns_frame_info.n_subblocks; s++) {
			tns_info = tns_frame_info.info[s];

			if ((tns_info.n_filt = (int)audio_stream.next_bits( short_flag ? 1 : 2 )) == 0)
				continue;
	    
			tns_info.coef_res = res = (int)audio_stream.next_bits( 1 ) + 3;
			top = info.sfb_per_sbk[s];
			tns_filt = tns_info.filt;
			tns_filt_index = 0;
			for (f = tns_info.n_filt; f > 0; f--)  {
				tns_filt[tns_filt_index].stop_band = top;
				top = tns_filt[tns_filt_index].start_band = top - (int)audio_stream.next_bits( short_flag ? 4 : 6 );
				tns_filt[tns_filt_index].order = (int)audio_stream.next_bits( short_flag ? 3 : 5 );

				if (tns_filt[tns_filt_index].order > 0)  {
					tns_filt[tns_filt_index].direction = (int)audio_stream.next_bits( 1 );
					compress = (int)audio_stream.next_bits( 1 );

					res2 = res - compress;
					s_mask = sgn_mask[ res2 - 2 ];
					n_mask = neg_mask[ res2 - 2 ];

					sp = 0;
					for (t=tns_filt[tns_filt_index].order; t>0; t--)  {
						tmp = (short)audio_stream.next_bits( res2 );
						tns_filt[tns_filt_index].coef[sp++] = (short)(((tmp & s_mask) > 0) ? (tmp | n_mask) : tmp);
					}
				}
				tns_filt_index++;
			}
		}   /* subblock loop */
		return 1;
	}

	void get_nec_nc(Nec_Info nec_info) throws IOException {
		int i;
//		System.out.println("get_nec_nc");
		nec_info.number_pulse = (int)audio_stream.next_bits(Constants.LEN_NEC_NPULSE);
		nec_info.pulse_start_sfb = (int)audio_stream.next_bits(Constants.LEN_NEC_ST_SFB);
//		System.out.println("number_pulse = " + nec_info.number_pulse);
//		System.out.println("pulse_start_sfb = " + nec_info.pulse_start_sfb);
		for(i=0; i < nec_info.number_pulse; i++) {
			nec_info.pulse_offset[i] = (int)audio_stream.next_bits(Constants.LEN_NEC_POFF);
			nec_info.pulse_amp[i] = (int)audio_stream.next_bits(Constants.LEN_NEC_PAMP);
//			System.out.println("nec_info.pulse_offset[" + i + "] = " + nec_info.pulse_offset[i]);
//			System.out.println("nec_info.pulse_amp[" + i + "] = " + nec_info.pulse_amp[i]);
		}
	}

	void nec_nc(float[] coef, Nec_Info nec_info)
	{
		int i, k;
    
		/* use long sfb table even for short blocks! */
		k = only_long_info.sbk_sfb_top[0][nec_info.pulse_start_sfb];
    
		for(i=0; i<=nec_info.number_pulse; i++) {
			k += nec_info.pulse_offset[i];
			if (coef[k]>0) { 
				coef[k] += nec_info.pulse_amp[i];
			} else { 
				coef[k] -= nec_info.pulse_amp[i];
			}
		}
	}
/*
	if(!getics(info, common_window, win[widx], wshape[widx].this_bk,
		group[widx], max_sfb[widx], lpflag[widx], prstflag[widx], 
		nsect, sect, coef[i], factors[i-first], tns[i]))
*/
	int getics(int widx, Info info, boolean common_window, byte[] win, Wnd_Shape[] wshape, 
		byte[] group, byte[] max_sfb, int[] lpflag, int[] prstflag, 
		byte[] sect, float[] coef, short[] factors, TNS_frame_info tns) throws IOException {
		int i, tot_sfb;

		/*
		 * global gain
		 */
		global_gain = (int)audio_stream.next_bits(Constants.LEN_SCL_PCM);
/*		if (debug[Constants.DEBUG_F])
			System.out.println("global gain: " + global_gain);	*/

		if (! common_window) {
/*
	void get_ics_info(int widx, byte[] win, Wnd_Shape[] wshape, byte[] group,
			byte[] max_sfb, int[] lpflag, int[] prstflag) {
 */			
			get_ics_info(widx, win, wshape, group, max_sfb, lpflag, prstflag);
		}
		info.copyFields(winmap[win[widx]]);

		/* calculate total number of sfb for this grouping */
		if (max_sfb[widx] == 0) {
			tot_sfb = 0;
		} else {
			i = 0;
			tot_sfb = info.sfb_per_sbk[0];
/*			if (debug[Constants.DEBUG_F])
				System.out.println("tot sfb " + i + " " + tot_sfb); */
			while (group[i++] < info.nsbk) {
				tot_sfb += info.sfb_per_sbk[0];
/*				if (debug[Constants.DEBUG_F])
					System.out.println("tot sfb " + i + " " + tot_sfb); */
			}
		}

		/* 
		 * section data
		 */
		nsect = huffcb(sect, info.sectbits, tot_sfb, info.sfb_per_sbk[0], max_sfb[widx]);
		if((nsect == 0) && (max_sfb[widx] > 0))
			return 0;

		/* calculate band offsets
		 * (because of grouping and interleaving this cannot be
		 * a constant: store it in info.bk_sfb_top)
		 */
		calc_gsfb_table(info, group);

		/*
		 * scale factor data
		 */
		if(hufffac(info, group, sect, factors) == 0)
			return 0;

		/*
		 * NEC noiseless coding
		 */
		if (nec_info.pulse_data_present = audio_stream.next_bit()) {
			get_nec_nc(nec_info);
		}

		/*
		 * tns data
		 */
		if (audio_stream.next_bit()) {
			get_tns(info, tns);
		}
		else {
			clr_tns(info, tns);
		}

		/*
		 * Sony gain control
		*/
		if (audio_stream.next_bit()) {
			throw new IOException("Gain control not implmented");
		}
		
		return huffspec(info, sect, factors, coef);
	}

	/*
	 * read the codebook and boundaries
	 */
	int	huffcb(byte[] sect, int[] sectbits, int tot_sfb, int sfb_per_sbk, byte max_sfb) throws IOException {
		int nsect, n, base, bits, len;
		int sect_index = 0;
/*		if (debug[Constants.DEBUG_S]) {
			System.out.println("total sfb " + tot_sfb);
			System.out.println("sect, top, cb");
		} */
		bits = sectbits[0];
		len = (1 << bits) - 1;
		nsect = 0;
		for(base = 0; base < tot_sfb && nsect < tot_sfb; ){
			sect[sect_index++] = (byte)audio_stream.next_bits(Constants.LEN_CB);

			n = (int)audio_stream.next_bits(bits);
			while(n == len && base < tot_sfb){
				base += len;
				n = (int)audio_stream.next_bits(bits);
			}
			base += n;
			sect[sect_index++] = (byte)base;
			nsect++;
/*			if (debug[Constants.DEBUG_S])
				System.out.println(" " + nsect + " " + sect[sect_index - 1] + " " + sect[sect_index - 2]);
*/
			/* insert a zero section for regions above max_sfb for each group */
			if ((sect[sect_index - 1] % sfb_per_sbk) == max_sfb) {
				base += (sfb_per_sbk - max_sfb);
				sect[sect_index++] = 0;
				sect[sect_index++] = (byte)base;
				nsect++;
/*				if (debug[Constants.DEBUG_S])
					System.out.println("(" + nsect + " " + sect[sect_index - 1] + " " + sect[sect_index - 2]);
*/					
			}
		}

		if(base != tot_sfb || nsect > tot_sfb) {
			return 0;
		}
		return nsect;
	}

	/* 
	 * get scale factors
	 */
	int	hufffac(Info info, byte[] group, byte[] sect, short[] factors) throws IOException {
		Hcb hcb;
		int[][] hcw;
		int i, b, bb, t, n, sfb, top, fac, is_pos;
		int[] fac_trans = new int[Constants.MAXBANDS]; 
		int group_idx = 0;
		int sect_index = 0;
		int factors_index = 0;
		int fac_trans_index = 0; 

		/* clear array for the case of max_sfb == 0 */
		for(i = 0; i < Constants.MAXBANDS; i++) {
			factors[i] = 0;
			fac_trans[i] = 0;
		}

		sfb = 0;
		for(i = 0; i < nsect; i++){
			top = sect[sect_index + 1];		/* top of section in sfb */
			t = sect[sect_index];		/* codebook for this section */
			sect_index += 2;
			for(; sfb < top; sfb++) {
				fac_trans[sfb] = t;
			}
		}

		/* scale factors are dpcm relative to global gain
	 	* intensity positions are dpcm relative to zero
	 	*/
		fac = global_gain;
		is_pos = 0;

		/* get scale factors */
		hcb = huffman.book[Constants.BOOKSCL];
		hcw = hcb.hcw;
		bb = 0;
/*		if (debug[Constants.DEBUG_F]) {
			System.out.println("scale factors");
		} 
		System.out.println("info.nsbk : " + info.nsbk);
		System.out.println("group_idx : " + group_idx);
		System.out.println("factors_index : " + factors_index);
*/		
		for(b = 0; b < info.nsbk; ){
			n = info.sfb_per_sbk[b];
			b = group[group_idx++];
			/*			
			System.out.println("n : " + n);
			System.out.println("b : " + b);
*/			
			for(i = 0; i < n; i++){
//				System.out.println("fac_trans[" + fac_trans_index + i + "] = " + fac_trans[fac_trans_index + i]);
				if(fac_trans[fac_trans_index + i] > 0){
				/* decode intensity positions */
					if ( (info.nsbk==1) &&	    /* No short blocks yet! */
					( (fac_trans[fac_trans_index + i] == Constants.INTENSITY_HCB) || 
						(fac_trans[fac_trans_index + i] == Constants.INTENSITY_HCB2) ) ) {

							System.out.println(2);
						/* decode intensity position */
						t = huffman.decode_huff_cw(hcw);
						is_pos += t - Constants.MIDFAC;
/*						if (debug[Constants.DEBUG_F]) {
							System.out.println(i + " " + is_pos + " (is_pos)");
						} */
						factors[factors_index + i] = (short)is_pos;
						continue;
					}
					/* decode scale factor */
					t = huffman.decode_huff_cw(hcw);
					fac += t - Constants.MIDFAC;    /* 1.5 dB */
/*					if (debug[Constants.DEBUG_F]) {
						System.out.print(i + ":" + fac);
					} */
					if((fac >= 2*maxfac) || (fac < 0)) {
						return 0;
					}
	
					factors[factors_index + i] = (short)fac;
				}
			}
/*			if (debug[Constants.DEBUG_F]) {
				System.out.println();
			} */
			/* expand short block grouping */
			if (!info.islong) {
				for(bb++; bb < b; bb++) {
					for (i = 0; i < n; i++) {
						factors[factors_index + i+n] = factors[factors_index + i];
					}
					factors_index += n;
				}
			}
			fac_trans_index += n;
			factors_index += n;
		}
		return 1;
	}

	/* rm2 inverse quantization
	 * escape books need ftn call
	 * other books done via macro
	 */
	float iquant( int q ) {
		return ( q >= 0 ) ? (float)huffman.iq_exp_tbl[ q ] : (float)(-huffman.iq_exp_tbl[ - q ]);
	}

	float esc_iquant(int q)
	{
		if (q > 0) {
			if (q < Constants.MAX_IQ_TBL) {
				return((float)huffman.iq_exp_tbl[q]);
			} else {
				return (float)(Math.pow(q, 4d/3d));
			}
		} else {
			q = -q;
			if (q < Constants.MAX_IQ_TBL) {
				return((float)(-huffman.iq_exp_tbl[q]));
			} else {
				return(float)(-Math.pow(q, 4d/3d));
			}
		}
	}

	int	huffspec(Info info, byte[] sect, short[] factors, float[] coef)  throws IOException  {
		Hcb hcb;
		int[][] hcw;
		int i, j, k, table, step, temp, stop, bottom, top;
		short[] bands;
		int bands_index = 0; 
		int[] quant = new int[Constants.LN2];

		int[] tmp_spec = new int[Constants.LN2];
		int sect_index = 0;
		int quant_index = 0;

		for(i = 0; i < Constants.LN2; i++) {
			quant[i] = 0;
		}

		bands = info.bk_sfb_top;
		bottom = 0;
		k = 0;
		for(i = nsect; i > 0; i--) {
			table = sect[sect_index + 0];
			top = sect[sect_index + 1];
			sect_index += 2;
			if( (table == 0) ||
				(table == Constants.INTENSITY_HCB) ||
				(table == Constants.INTENSITY_HCB2) ) {
					bands_index = top;
				k = bands[bands_index-1];
				bottom = top;
				continue;
			}
			if(table < (Constants.BY4BOOKS+1)) {
				step = 4;
			} else {
				step = 2;
			}
			hcb = huffman.book[table];
			hcw = hcb.hcw;
			quant_index = k;

			for(j=bottom; j<top; j++) {
				stop = bands[bands_index++];
				while(k < stop) {
					temp = huffman.decode_huff_cw(hcw);
					huffman.unpack_idx(quant, quant_index, temp, hcb);

					if (!hcb.signed_cb) {
						huffman.get_sign_bits(quant, quant_index, step);
					}
					if(table == Constants.ESCBOOK){
						quant[quant_index + 0] = getescape(quant[quant_index + 0]);
						quant[quant_index + 1] = getescape(quant[quant_index + 1]);
					}
					quant_index += step;
					k += step;
				}
/*				
				if(debug[Constants.DEBUG_Q]){
					System.out.println("sect " + table + " " + kstart);
					for (idx=kstart ;idx<k;idx++) {
					   System.out.print(quantDebug[idx] + " ");
					}
					System.out.println();
				}
*/										
			}
			bottom = top;
		}

		/* NEC noisless coding reconstruction */
		if (nec_info.pulse_data_present) {
			nec_nc(coef, nec_info);
		}

		if (!info.islong) {
			deinterleave (quant,tmp_spec,
				info.num_groups,   
				info.group_len,
				info.sfb_per_sbk,
				info.sfb_width_128);
			for(i = 0; i < tmp_spec.length; i++) {
				quant[i] = tmp_spec[i];
			}
		}

//		System.out.print("coef: ");
		/* inverse quantization */
		for (i=0; i<info.bins_per_bk; i++) {
			coef[i] = esc_iquant(quant[i]);
//			System.out.print(coef[i] + " ");
		}
//		System.out.println();

		/* rescaling */
		{
			int sbk, nsbk, sfb, nsfb, fac, top_coef;
			float scale;
			int coef_index = 0;

			i = 0;
			nsbk = info.nsbk;
			for (sbk=0; sbk<nsbk; sbk++) {
				nsfb = info.sfb_per_sbk[sbk];
				k=0;
				for (sfb=0; sfb<nsfb; sfb++) {
					top_coef = info.sbk_sfb_top[sbk][sfb];
					fac = factors[i++]-Constants.SF_OFFSET;

					if ((fac >= 0) && (fac < Constants.TEXP)) {
						scale = huffman.exptable[fac];
					} else {
						if (fac == -Constants.SF_OFFSET) {
							scale = 0;
						} else {
							scale = (float)Math.pow( 2.0,  0.25*fac );
						}
					}
					for ( ; k<top_coef; k++) {
						coef[coef_index++] *= scale;
					}
				}
			}
		}

		return 1;
	}
	
	
	/** checked */
	int getescape(int q) throws IOException {
		int i, off, neg;

		if(q < 0){
			if(q != -16)
				return q;
			neg = 1;
		} else{
			if(q != +16)
				return q;
			neg = 0;
		}

		for(i = 4;; i++){
			if(!audio_stream.next_bit())
				break;
		}

		if(i > 16){
			off = (int)audio_stream.next_bits(i-16) << 16;
			off |= audio_stream.next_bits(16);
		} else {
			off = (int)audio_stream.next_bits(i);
		}

		i = off + (1 << i);
		if(neg > 0) {
			i = -i;
		}
		return i;
	}	

	/* original source intensity.c */ 	
	/**
	 * if (chan==RIGHT) { 
	 *     do IS decoding for this channel (scale left ch. values with factor(SFr-SFl) )
	 *     reset all lpflags for which IS is on
	 *     pass decoded IS values to predict
	 * }
	 */
	void intensity(MC_Info mip, Info info, int widx, int[] lpflag, int ch, float[][] coef)
	{
		int left, right, i, k, nsect, sign, bot, top, sfb, ktop;
		float scale;
		Ch_Info cip = mip.ch_info[ch];
		IS_Info iip = mip.ch_info[ch].is_info;

		if (!(cip.cpe && iip.is_present && !cip.ch_is_left)) {
			return;
		}
	
		left = cip.paired_ch;
		right = ch;

		nsect = iip.n_is_sect;
		for (i=0; i<nsect; i++) {
			sign = iip.sign[i];
			top = iip.top[i];
			bot = iip.bot[i];
			for (sfb=bot; sfb<top; sfb++) {
				/* disable prediction */
				lpflag[1+sfb] = 0;

				scale = (float)(sign * Math.pow( 0.5,  0.25*(iip.fac[sfb]) ));

				/* reconstruct right intensity values */
/*				
				if (debug[Constants.DEBUG_I]) {
					System.out.println("applying IS coding of " + scale + " on ch " + ch + " at sfb " + sfb);
				}
*/						
				k = (sfb==0) ? 0 : info.bk_sfb_top[sfb-1];
				ktop = info.bk_sfb_top[sfb];
				for ( ; k<ktop; k++) {
					coef[right][k] = coef[left][k] * scale;
				}
			}
		}
	}
	
	/* original source stereo.c */ 	
	void synt(Info info, byte[] group, byte[] mask, float[] right, float[] left) {
		float vrr, vrl;
		short[] band;
		int i, n, nn, b, bb, nband;
		int group_index = 0;
		int right_index = 0;
		int left_index = 0;
		int mask_index = 0;

		/*mask is grouped */
		bb = 0;
		for(b = 0; b < info.nsbk; ){
			nband = info.sfb_per_sbk[b];
			band = info.sbk_sfb_top[b];

			b = group[group_index++];		/*b = index of last sbk in group */
			for(; bb < b; bb++){	/* bb = sbk index */
				n = 0;
				for(i = 0; i < nband; i++){
					nn = band[i];	/* band is offset table, nn is last coef in band */
					if(mask[mask_index + i] > 0){
						for(; n < nn; n++){	/* n is coef index */
							vrr = right[right_index + n];
							vrl = left[left_index + n];
							left[left_index + n] = vrr + vrl;
							right[right_index + n] = vrl - vrr;
						}
					}
					n = nn;
				}
				right_index += info.bins_per_sbk[bb];
				left_index += info.bins_per_sbk[bb];
			}
			mask_index += info.sfb_per_sbk[bb-1];
		}
	}
	
//	byte[] AU_ptr;
//	long  AU_idx;
//	long  AU_len;

	void init() throws IOException {
//		if(!Constants.DOLBY_MDCT) {
//			imdctinit();
//		}
		predinit();
//		restarttio();

		winmap[0] = win_seq_info[Constants.ONLY_LONG_WINDOW];
		winmap[1] = win_seq_info[Constants.ONLY_LONG_WINDOW];
		winmap[2] = win_seq_info[Constants.EIGHT_SHORT_WINDOW];
		winmap[3] = win_seq_info[Constants.ONLY_LONG_WINDOW];
	}

	void predinit() throws IOException {
		int i, ch;
		for (ch = 0; ch < Constants.Chans; ch++) {
			for (i = 0; i < Constants.LN2; i++) {
				Monopred.init_pred_stat(sp_status[ch][i],
				Constants.PRED_ORDER,Constants.PRED_ALPHA,Constants.PRED_A,Constants.PRED_B);
				prev_quant[ch][i] = 0;
			}
		}
	}

	int getdata(byte[] data_bytes) throws IOException
	{
		boolean byte_align_flag;
		int d_cnt;
		audio_stream.next_bits(Constants.LEN_TAG);
		byte_align_flag = audio_stream.next_bit();

		if ((d_cnt = (int)audio_stream.next_bits(8)) == (1<<8)-1) {
			d_cnt +=  (int)audio_stream.next_bits(8);
		}
		if (byte_align_flag) {
			audio_stream.byteAlign(); 
		}

		for (int i=0; i<d_cnt; i++) {
			data_bytes[i] = (byte)audio_stream.next_bits(Constants.LEN_BYTE);
		}

		return 0;
	}


	/*
	int
	getdata(int *tag, int *dt_cnt, byte *data_bytes)
	{
		int i, cnt;

		*tag = getbits(LEN_TAG);
		if ((cnt = getbits(LEN_D_CNT)) == (1<<LEN_D_CNT)-1)
		cnt +=  getbits(LEN_D_ESC);
		*dt_cnt = cnt;
		if (debug['x'])
		PRINT(SE, "data element %d has %d bytes\n", *tag, cnt);

		for (i=0; i<cnt; i++)
		data_bytes[i] = getbits(LEN_BYTE);

		return 0;
	}
	*/

	void getfill() throws IOException {
		int i, cnt;

		if ((cnt = (int)audio_stream.next_bits(Constants.LEN_F_CNT)) == (1<<Constants.LEN_F_CNT)-1) {
			cnt +=  (int)audio_stream.next_bits(Constants.LEN_F_ESC) - 1;
		}
/*		if (debug[Constants.DEBUG_X]) {
			System.out.print("fill element has " + cnt + " bytes");
		}	*/
		for (i=0; i<cnt; i++) {
			audio_stream.next_bits(Constants.LEN_BYTE);
		}
	}

/*
	public static void main(String[] argv) {

		try {
			BitStream audio_stream = new BitStream(new FileInputStream(argv[0]));
			AACDecoder decoder = new AACDecoder(audio_stream);
			decoder.initio(argv);	    // parse command line 
			decoder.init();		    // initialize data structures 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
*/	


	public int decodeFrame(byte[] buf) throws IOException
	{
		int i, j,  ch, wn, ele_id;
		int left, right;
		Info info;
		MC_Info mip = mc_info;
		Ch_Info cip;

/*		
		AU_ptr = AUptr;
		AU_idx = 0;
		AU_len = AUlen;
*/		
		/*PRINT( SE, "\n==== AU_len = %d\n", AU_len );*/
/*		if(debug[Constants.DEBUG_N]) {
			System.out.print("\rblock " + bno);
		}		*/
	
		/* call transport layer */
		if(startblock() < 0)
			return -1; //myexit(0);
	
		config.reset_mc_info(mip);
		while ((ele_id=(int)audio_stream.next_bits(Constants.LEN_SE_ID)) != Constants.ID_END) {
			/* get audio syntactic element */
/*			if(debug[Constants.DEBUG_V])
				System.out.println("\nele_id " + ele_id);
*/
			switch (ele_id) {
			case Constants.ID_SCE:		/* single channel */
			case Constants.ID_CPE:		/* channel pair */
			case Constants.ID_LFE:		/* low freq effects channel */
				int result_code = huffdecode(ele_id, mip, wnd, wnd_shape, group, 
				hasmask, mask, max_sfb, lpflag, prstflag, tns_frame_info,
				coef); 
				if (result_code < 0)
					throw new IOException("huffdecode returned " + result_code);
				break;
			case Constants.ID_DSE:		/* data element */
				if (getdata(d_bytes) < 0)
					throw new IOException("data channel");
				break;
			case Constants.ID_PCE:		/* program config element */
//				skip config element			
//				config.get_prog_config(prog_config);
				break;
			case Constants.ID_FIL:		/* fill element */
				getfill();
				break;
			case Constants.ID_CCE:		/* coupling channel */
/*
 				if(Constants.CChans > 0) {
					if (getcc(mip, cc_wnd, cc_wnd_shape, cc_coef, cc_gain) < 0)
						throw new IOException("getcc");
					break;
				}
*/				
				System.out.println("Coupling channels isn't supported!");
				break;
			default:
				System.out.println("Element not supported: " + ele_id);
				break;
			}
		}
		config.check_mc_info(mip, (bno==0 && default_config));

/*		// call transport layer 
		if(endblock() < 0) {
			throw new IOException("endblock");
		}
*/	
//		if(Constants.ICChans > 0) {
			/* transform independently switched coupling channels */
//			ind_coupling(mip, wnd, wnd_shape, cc_wnd, cc_wnd_shape, cc_coef);
//		}
	
		/* m/s stereo */
		for (ch=0; ch < Constants.Chans; ch++) {

			cip = mip.ch_info[ch];
			if ((cip.present) && (cip.cpe) && (cip.ch_is_left)) {
				wn = cip.widx;
				if(hasmask[wn] > 0) {
					left = ch;
					right = cip.paired_ch;
					info = winmap[wnd[wn]];
					synt(info, group[wn], mask[wn], coef[right], coef[left]);
				}
			}
		}
	
		/* intensity stereo and prediction */
		for (ch=0; ch < Constants.Chans; ch++) {
			if (!(mip.ch_info[ch].present)) 
				continue;

			wn = mip.ch_info[ch].widx;
			info = winmap[wnd[wn]];
			intensity(mip, info, wn, lpflag[wn], ch, coef);
			Monopred.predict(info, mip.profile, lpflag[wn], sp_status[ch], prev_quant[ch], coef[ch]);
		}
	
		for (ch=0; ch < Constants.Chans; ch++) {
			if (!(mip.ch_info[ch].present)) { 
				continue;
			}
			wn = mip.ch_info[ch].widx;
			info = winmap[wnd[wn]];
		
			/* predictor reset */
			left = ch;
			right = left;
			if ( (mip.ch_info[ch].cpe) &&
				(mip.ch_info[ch].common_window) )
				/* prstflag's shared by channel pair */
				right = mip.ch_info[ch].paired_ch;
			Monopred.predict_reset(info, prstflag[wn], sp_status, prev_quant, 
				left, right);
		
//			if(Constants.CChans > 0) {
				/* if cc_domain indicates before TNS */
//				coupling(mip, coef, cc_coef, cc_gain, ch, Constants.CC_DOM, Constants.CC_IND == 0);
//			}
		
			/* tns */
			for (i=j=0; i< tns_frame_info[ch].n_subblocks; i++) {
/*				if (debug[Constants.DEBUG_T]) {
					System.out.println(bno + " " + ch + " " + i);
					tns.print_tns( tns_frame_info[ch].info[i]);
				}	*/
			
				// @TODO check the following code
				tns.tns_decode_subblock(coef[ch], j,
					info.sfb_per_sbk[i],
					info.sbk_sfb_top[i],
					info.islong,
					tns_frame_info[ch].info[i] );
			
				j += info.bins_per_sbk[i];
			}
		
//			if(Constants.CChans > 0) {
				/* if cc_domain indicated after TNS */
//				coupling(mip, coef, cc_coef, cc_gain, ch, Constants.CC_DOM, Constants.CC_IND == 0);
//			}
			if(Constants.DOLBY_MDCT) {
				/* inverse transform */
				dolby_Adapt.freq2time_adapt (wnd [wn], wnd_shape [wn], coef [ch], state [ch], data [ch]);
//			} else {
//				imdct(wnd[wn], wnd_shape[wn].this_bk, coef[ch], state[ch], data[ch]);
//				
				/* scale imdct output */
//				float scale = (float)(1 / Math.sqrt (2));
//				for (i = 0; i < Constants.LN2; i++) {
//					data[ch][i] *= scale;
//				}
			}

/*
			System.out.print("coef[" + ch + "] = ");
			for(i = 0; i < 10; i++) {
				System.out.print(coef[ch][i] + " ");
			}
			System.out.println();

			System.out.print("state[" + ch + "] = ");
			for(i = 0; i < 10; i++) {
				System.out.print(state[ch][i] + " ");
			}
			System.out.println();
			System.out.print("data[" + ch + "] = ");
			for(i = 0; i < 10; i++) {
				System.out.print(data[ch][i] + " ");
			}
			System.out.println();
*/
//			if(Constants.CChans > 0) {
				/* independently switched coupling */
//				coupling(mip, coef, cc_coef, cc_gain, ch, Constants.CC_DOM, Constants.CC_IND);
//			}
		}
		


		/* skip first two blocks so output is time aligned with input */
		if (bno > 1) {
			writeout(data, mip, buf);
		}
		bno ++;
		return bno > 2 ? (2048 * mc_info.nch) : 0;
	}
	
	void writeout(float[][] data, MC_Info mip, byte[] obuf )
	{
		int i,p_index = 0;
		for (i=0; i<Constants.Chans; i++) {
			if (!(mip.ch_info[i].present))
				continue;
		
			fmtchan(obuf, p_index, data[i], 2 * mc_info.nch);
			p_index += 2;
		}
	}
	
	void fmtchan(byte[] p, int p_index, float[] data, int stride)
	{
		int i, c, data_index = 0;
		float s;

		for(i=0; i < Constants.LN2; i++) {
			s = data[data_index++];
			if(s < 0) {
				s -= .5;
				if(s < -0x7fff)
					s = (float) -0x7fff;
			} else {
				s += .5;
				if(s > 0x7fff)
					s = (float) 0x7fff;
			}
			c = (int) s;
			p[p_index + 1] = (byte)((c >> 8) & 0xff);
			p[p_index + 0] = (byte)(c & 0xff);
			p_index += stride;
		}
	}
	
	
	private boolean adif_header_present = false;
	
	int startblock() throws IOException {
    	/* get adif header */
    	if (adif_header_present) {
			if (config.get_adif_header() < 0) 
		    	return -1;
			adif_header_present = false;
    	}
    
    	audio_stream.byteAlign();	/* start of block is byte aligned */
//		audio_stream.print_next_bits(48);
    	return 1;
	}
	
/*	
	// source portio.c 
	void initio(String[] argv) throws IOException {
		int i, j;
		
		// set defaults 
		adif_header_present = false;
		current_program = -1;
		default_config = true;
		mc_info.profile = Constants.Main_Profile;
		mc_info.sampling_rate_idx = Constants.Fs_48;

		// save cmd 
		// ARGBEGIN is so clever that it throws away argv[0] and 
		// increments the argv pointer
		//

//
//		if(argv.length != 2) {
//			usage(cmd);
//		}
//
		for(i = 0; i < argv.length; i++,i++) {
			if(argv[i].length() > 1) {
				usage();
				throw new IOException("unknown option");
			}
			switch(argv[i].charAt(0)) {
				case 'i':
					adif_header_present = true;
					current_program = (argv[i + 1] == null) ? -1 : Integer.parseInt(argv[i + 1]);
					if (current_program > ((1<<Constants.LEN_NUM_PCE)-1)) {
						throw new IOException("Invalid program: " + current_program);
					}
					break;
				case 'p':
					if ("Main".equalsIgnoreCase(argv[i + 1])) {
						mc_info.profile = Constants.Main_Profile; 
						break;
					}
					if ("LC".equalsIgnoreCase(argv[i + 1])) {
						mc_info.profile = Constants.LC_Profile; 
						break;
					}
					throw new IOException("Unsupported profile " + argv[i + 1]);
				case 's':
					j = Integer.parseInt(argv[i + 1]);
					for (i=0; i<(1<<Constants.LEN_SAMP_IDX); i++) {
						if (j == Tables.samp_rate_info[i].samp_rate)
							break;
					}
					if (i == (1<<Constants.LEN_SAMP_IDX)) {
						throw new IOException("Unsupported sampling frequency " + j);
					}
					mc_info.sampling_rate_idx = i;
					break;
				case 'D':
					String options = argv[i + 1];
					for(j = 0; j < options.length(); j++) {
						char debug_option = options.charAt(j);
						for(int z = 0; z < Constants.debug_options.length; z++) {
							if(Constants.debug_options[z] == debug_option) {
								debug[z] = true;
								break;
							}
						}
					}

					break;
			
				default:
					usage();
					throw new IOException("unknown option");
			}
		}
	}
	
	void usage() {
	    System.out.println("usage: [options] chan_file pcm_file");
    	System.out.println("	options are:");
    	System.out.println("	-a create AIFF output files");
    	System.out.println("	-i [prog_tag] ADIF header present (first prog is default)");
    	System.out.println("	-p profile (Main or LC, Main is default)");
    	System.out.println("	-s sampling_frequency, Hz (48 kHz is default)");
    	System.out.println("	-D[a-Z] (enable debugging printouts)");
	}

*/
/*	
	long f2ir(float x)
	{
		if(x >= 0) {
			return (long) (x+.5);
		}
		return -(long)(-x + .5);
	}

	void fltcpy(float[] dp1, float[] dp2, int cnt)
	{
		for(int i = 0;i < cnt; i++) {
			dp2[i] = dp1[i];
		}
	}

	void fltset(float[] dp1, float dval, int cnt)
	{
		for(int i = 0; i < cnt; i++) {
			dp1[i] = dval;
		}
	}

	void fltclr(float[] dp1, int cnt)
	{
		for(int i = 0; i < cnt; i++) {
			dp1[i] = 0;
		}
	}

	void intcpy(int[] ip1, int[] ip2, int cnt)
	{
		for(int i = 0;i < cnt; i++) {
			ip2[i] = ip1[i];
		}
	}

	void intclr(int[] ip1, int cnt)
	{
		for(int i = 0; i < cnt; i++) {
			ip1[i] = 0;
		}
	}

	void byteclr(byte[] ip1, int cnt)
	{
		for(int i = 0; i < cnt; i++) {
			ip1[i] = 0;
		}
	}
*/	
	public Huffman getHuffman() {
		return huffman;
	}

	public BitStream getAudio_Stream() {
		return audio_stream;
	}

	public Config getConfig() {
		return config;
	}
	
	public int getSampleFrequency() {
		return mc_info.sampling_rate;
	}

	public int getChannelCount() {
		return mc_info.nch;
	}
	
	public String getAudioProfile() {
		switch(prog_config.profile) {
			case Constants.Main_Profile:
				return "Main Profile";
			case Constants.LC_Profile:
				return "LC Profile";
			case Constants.SRS_Profile:
				return "SRC Profile";
			default:
				return "Unknown Profile";
		}
	}

}