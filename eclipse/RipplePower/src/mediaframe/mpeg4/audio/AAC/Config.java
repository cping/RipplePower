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

/* original source config.c */
/**
 * Config
 */
public class Config {

	private Huffman huffman;

	private BitStream audio_stream;

	private AACDecoder decoder;

	public Config(AACDecoder decoder) throws IOException {
		super();
		this.decoder = decoder;
		this.huffman = decoder.getHuffman();
		this.audio_stream = decoder.getAudio_Stream();
		AudioSpecificConfig();
	}

	private byte audioObjectType;

	private byte sampleFrequencyIndex;

	private int sampleFrequency;

	private int channelConfiguration;

	private void AudioSpecificConfig() throws IOException {
		audioObjectType = (byte) audio_stream.next_bits(5);
		switch (audioObjectType) {
		case 1:
			decoder.prog_config.profile = Constants.Main_Profile;
			break;
		case 2:
			decoder.prog_config.profile = Constants.LC_Profile;
			break;
		case 3:
			decoder.prog_config.profile = Constants.SRS_Profile;
			break;
		default:
			throw new IOException("Unsupported profile:" + audioObjectType);
		}
		sampleFrequencyIndex = (byte) audio_stream.next_bits(4);
		if (sampleFrequencyIndex == 0x0f) {
			decoder.prog_config.sampling_rate = sampleFrequency = (int) audio_stream
					.next_bits(24);
			if (sampleFrequency >= 92017) {
				sampleFrequencyIndex = 0;
			} else if (sampleFrequency >= 75132) {
				sampleFrequencyIndex = 1;
			} else if (sampleFrequency >= 55426) {
				sampleFrequencyIndex = 2;
			} else if (sampleFrequency >= 46009) {
				sampleFrequencyIndex = 3;
			} else if (sampleFrequency >= 37566) {
				sampleFrequencyIndex = 4;
			} else if (sampleFrequency >= 27713) {
				sampleFrequencyIndex = 5;
			} else if (sampleFrequency >= 23004) {
				sampleFrequencyIndex = 6;
			} else if (sampleFrequency >= 18783) {
				sampleFrequencyIndex = 7;
			} else if (sampleFrequency >= 13856) {
				sampleFrequencyIndex = 8;
			} else if (sampleFrequency >= 11502) {
				sampleFrequencyIndex = 9;
			} else if (sampleFrequency >= 9391) {
				sampleFrequencyIndex = 10;
			} else {
				sampleFrequencyIndex = 11;
			}
		} else {
			decoder.prog_config.sampling_rate = sampleFrequency = Tables.SampleIndexRateTable[sampleFrequencyIndex];
		}
		decoder.prog_config.sampling_rate_idx = sampleFrequencyIndex;
		channelConfiguration = (int) audio_stream.next_bits(4);
		switch (channelConfiguration) {
		case 0:
			break;
		case 1:
			// mono
			decoder.prog_config.front.num_ele = 1;
			decoder.prog_config.front.ele_is_cpe[0] = 0;
			break;
		case 2:
			// stereo
			decoder.prog_config.front.num_ele = 1;
			decoder.prog_config.front.ele_is_cpe[0] = 1;
			break;
		default:
			throw new IOException("Unsupported channel configuration:"
					+ channelConfiguration);
		}
		/*
		 * System.out.println("audioObjectType = " + audioObjectType);
		 * System.out.println("sampleFrequencyIndex = " + sampleFrequencyIndex);
		 * System.out.println("sampleFrequency = " + sampleFrequency);
		 * System.out.println("channelConfiguration = " + channelConfiguration);
		 */
		GASpecificConfig();
		audio_stream.alignHeader();
	}

	private int frameLength;

	private boolean frameLengthFlag;

	private boolean dependsOnCoreCoder;

	private int coreCoderDelay;

	private boolean extensionFlag;

	private boolean extensionFlag3;

	private void GASpecificConfig() throws IOException {
		frameLengthFlag = audio_stream.next_bit();
		if (frameLengthFlag) {
			frameLength = 960;
		} else {
			frameLength = 1024;
		}
		dependsOnCoreCoder = audio_stream.next_bit();
		if (dependsOnCoreCoder) {
			coreCoderDelay = (int) audio_stream.next_bits(14);
		}
		extensionFlag = audio_stream.next_bit();
		if (channelConfiguration == 0) {
			get_prog_config(decoder.prog_config);
		} else {
			enter_mc_info(decoder.mc_info, decoder.prog_config);
		}
		if (extensionFlag) {
			extensionFlag3 = audio_stream.next_bit();
		}
		/*
		 * System.out.println("frameLengthFlag = " + frameLengthFlag);
		 * System.out.println("dependsOnCoreCoder = " + dependsOnCoreCoder);
		 * System.out.println("extensionFlag = " + extensionFlag);
		 */
	}

	/*
	 * profile dependent parameters
	 */
	int tns_max_bands(boolean islong) {
		return Tables.tns_max_bands_tab[decoder.mc_info.profile <= Constants.LC_Profile ? 0
				: 1][islong ? 0 : 1][decoder.mc_info.sampling_rate_idx];
	}

	/*
	 * int tns_max_bands(boolean islong) { int max_bands = 0; if (islong) { //
	 * long window switch (decoder.mc_info.profile) { case
	 * Constants.Main_Profile: case Constants.LC_Profile: switch
	 * (decoder.mc_info.sampling_rate_idx) { case Constants.Fs_48: max_bands =
	 * 49; break; case Constants.Fs_44: max_bands = 42; break; case
	 * Constants.Fs_32: max_bands = 40; break; } break; case
	 * Constants.SRS_Profile: max_bands = 26; break; } } else { // short window
	 * max_bands = 14; } return max_bands; }
	 */
	int tns_max_order(boolean islong) {
		if (islong) {
			switch (decoder.mc_info.profile) {
			case Constants.Main_Profile:
				return 20;
			case Constants.LC_Profile:
			case Constants.SRS_Profile:
				return 12;
			}
		} else {
			return 7;
		}
		return 0;
	}

	int max_indep_cc(int nch) {
		switch (decoder.mc_info.profile) {
		case Constants.Main_Profile:
			return (nch < 3) ? 0 : 1;
		case Constants.LC_Profile:
		case Constants.SRS_Profile:
			return 0;
		}
		return 0;
	}

	int max_dep_cc(int nch) {
		switch (decoder.mc_info.profile) {
		case Constants.Main_Profile:
			return (2 * nch / 5);
		case Constants.LC_Profile:
			return ((nch + 2) / 5);
		case Constants.SRS_Profile:
			return 0;
		}
		return 0;
	}

	int max_lfe_chn(int nch) {
		switch (decoder.mc_info.profile) {
		case Constants.Main_Profile:
			return (nch < 3) ? 0 : 2;
		case Constants.LC_Profile:
			return (nch < 3) ? 0 : 1;
		case Constants.SRS_Profile:
			return (nch < 5) ? 0 : 1;
		}
		return 0;
	}

	/*
	 * adif_header
	 */
	int get_adif_header() throws IOException {
		int i, n, tag, select_status;
		ProgConfig tmp_config = new ProgConfig();
		ADIF_Header p = decoder.adif_header;

		/* adif header */
		for (i = 0; i < Constants.LEN_ADIF_ID; i++) {
			p.adif_id[i] = (char) audio_stream.next_bits(Constants.LEN_BYTE);
		}
		p.adif_id[i] = 0; /* null terminated string */
		/* test for id */
		if (!"ADIF".equals(new String(p.adif_id).substring(0, 4))) {
			return -1; /* bad id */
		}

		/* copyright string */
		if ((p.copy_id_present = (int) audio_stream
				.next_bits(Constants.LEN_COPYRT_PRES)) == 1) {
			for (i = 0; i < Constants.LEN_COPYRT_ID; i++) {
				p.copy_id[i] = (char) audio_stream
						.next_bits(Constants.LEN_BYTE);
			}
			p.copy_id[i] = 0; /* null terminated string */
		}
		p.original_copy = (int) audio_stream.next_bits(Constants.LEN_ORIG);
		p.home = (int) audio_stream.next_bits(Constants.LEN_HOME);
		p.bitstream_type = (int) audio_stream.next_bits(Constants.LEN_BS_TYPE);
		p.bitrate = audio_stream.next_bits(Constants.LEN_BIT_RATE);

		/* program config elements */
		select_status = -1;
		n = (int) audio_stream.next_bits(Constants.LEN_NUM_PCE);
		for (i = 0; i < n; i++) {
			tmp_config.buffer_fullness = (p.bitstream_type == 0) ? audio_stream
					.next_bits(Constants.LEN_ADIF_BF) : 0;
			tag = get_prog_config(tmp_config);
			if (decoder.current_program < 0) {
				decoder.current_program = tag; /* default is first prog */
			}
			if (decoder.current_program == tag) {
				decoder.prog_config = tmp_config;
				select_status = 1;
			}
		}

		return select_status;
	}

	/*
	 * program configuration element
	 */
	void get_ele_list(EleList p, int enable_cpe) throws IOException {
		int i, j;
		for (i = 0, j = p.num_ele; i < j; i++) {
			if (enable_cpe > 0) {
				p.ele_is_cpe[i] = (int) audio_stream
						.next_bits(Constants.LEN_ELE_IS_CPE);
				// System.out.println("p.ele_is_cpe[" + i +"] = " +
				// p.ele_is_cpe[i]);
			}
			p.ele_tag[i] = (int) audio_stream.next_bits(Constants.LEN_TAG);
			// System.out.println("p.ele_tag[" + i +"] = " + p.ele_tag[i]);
		}
	}

	int get_prog_config(ProgConfig p) throws IOException {
		int i, j, tag;

		tag = (int) audio_stream.next_bits(4);

		p.profile = (int) audio_stream.next_bits(2);
		// p.sampling_rate_idx = (int)audio_stream.next_bits(4);
		audio_stream.next_bits(4);
		p.sampling_rate_idx = sampleFrequencyIndex;
		p.sampling_rate = Tables.SampleIndexRateTable[p.sampling_rate_idx];
		p.front.num_ele = (int) audio_stream.next_bits(4);
		p.side.num_ele = (int) audio_stream.next_bits(4);
		p.back.num_ele = (int) audio_stream.next_bits(4);
		p.lfe.num_ele = (int) audio_stream.next_bits(2);
		p.data.num_ele = (int) audio_stream.next_bits(3);
		p.coupling.num_ele = (int) audio_stream.next_bits(4);
		if ((p.mono_mix.num_ele = (int) audio_stream.next_bits(1)) == 1) {
			System.out.println("mono mixdown present");
			p.mono_mix.ele_tag[0] = (int) audio_stream.next_bits(4);
		}
		if ((p.stereo_mix.num_ele = (int) audio_stream.next_bits(1)) == 1) {
			p.stereo_mix.ele_tag[0] = (int) audio_stream.next_bits(4);
		}
		if ((p.matrix_mixdown_idx_present = audio_stream.next_bit()) == true) {
			p.matrix_mixdown_idx = (int) audio_stream.next_bits(2);
			p.pseudo_surround_enable = audio_stream.next_bit();
		}
		get_ele_list(p.front, 1);
		get_ele_list(p.side, 1);
		get_ele_list(p.back, 1);
		get_ele_list(p.lfe, 0);
		get_ele_list(p.data, 0);
		get_ele_list(p.coupling, 1);
		audio_stream.byteAlign();
		j = (int) audio_stream.next_bits(8);
		for (i = 0; i < j; i++) {
			p.comments[i] = (char) audio_stream.next_bits(8);
		}
		/*
		 * System.out.println("p.profile = " + p.profile);
		 * System.out.println("p.sampling_rate_idx = " + p.sampling_rate_idx);
		 * System.out.println("p.sampling_rate = " + p.sampling_rate);
		 * System.out.println("p.front.num_ele = " + p.front.num_ele);
		 * System.out.println("p.side.num_ele = " + p.side.num_ele);
		 * System.out.println("p.back.num_ele = " + p.back.num_ele);
		 * System.out.println("p.lfe.num_ele = " + p.lfe.num_ele);
		 * System.out.println("p.coupling.num_ele = " + p.coupling.num_ele);
		 */
		/* activate new program configuration if appropriate */
		if (decoder.current_program < 0)
			decoder.current_program = tag; /* always select new program */
		if (tag == decoder.current_program) {
			/* enter configuration into MC_Info structure */
			if (enter_mc_info(decoder.mc_info, decoder.prog_config) < 0) {
				return -1;
			}
			/* inhibit default configuration */
			decoder.default_config = false;
		}
		return tag;
	}

	/*
	 * enter program configuration into MC_Info structure only configures for
	 * channels specified in all.h
	 */
	int enter_mc_info(MC_Info mip, ProgConfig pcp) throws IOException {
		int i, j, cpe, tag;
		EleList elp;
		boolean cw;

		/* reset channel counts */
		mip.nch = 0;
		mip.nfch = 0;
		mip.nfsce = 0;
		mip.nsch = 0;
		mip.nbch = 0;
		mip.nlch = 0;
		mip.ncch = 0;

		/*
		 * profile and sampling rate re-configure if new sampling rate
		 */
		mip.profile = pcp.profile;
		mip.sampling_rate = pcp.sampling_rate;

		if (mip.sampling_rate_idx != pcp.sampling_rate_idx) {
			mip.sampling_rate_idx = pcp.sampling_rate_idx;
			decoder.infoinit(Tables.samp_rate_info[mip.sampling_rate_idx]);
		}

		cw = false; /* changed later */

		/* front elements, center out */
		elp = pcp.front;
		/* count number of leading SCE's */
		for (i = 0, j = elp.num_ele; i < j; i++) {
			if (elp.ele_is_cpe[i] > 0) {
				break;
			}
			mip.nfsce++;
		}
		for (i = 0, j = elp.num_ele; i < j; i++) {
			cpe = elp.ele_is_cpe[i];
			tag = elp.ele_tag[i];
			if ((enter_chn(cpe > 0, tag, 'f', cw, mip)) < 0)
				return (-1);
		}

		/* side elements, left to right then front to back */
		elp = pcp.side;
		for (i = 0, j = elp.num_ele; i < j; i++) {
			cpe = elp.ele_is_cpe[i];
			tag = elp.ele_tag[i];
			if ((enter_chn(cpe > 0, tag, 's', cw, mip)) < 0)
				return (-1);
		}

		/* back elements, outside to center */
		elp = pcp.back;
		for (i = 0, j = elp.num_ele; i < j; i++) {
			cpe = elp.ele_is_cpe[i];
			tag = elp.ele_tag[i];
			if ((enter_chn(cpe > 0, tag, 'b', cw, mip)) < 0)
				return (-1);
		}

		/* lfe elements */
		elp = pcp.lfe;
		for (i = 0, j = elp.num_ele; i < j; i++) {
			cpe = elp.ele_is_cpe[i];
			tag = elp.ele_tag[i];
			if ((enter_chn(cpe > 0, tag, 'l', cw, mip)) < 0) {
				return (-1);
			}
		}

		/* coupling channel elements */
		elp = pcp.coupling;
		for (i = 0, j = elp.num_ele; i < j; i++) {
			mip.cch_tag[i] = elp.ele_tag[i];
		}
		mip.ncch = j;

		/* mono mixdown elements */
		elp = pcp.mono_mix;
		if (elp.num_ele > 0) {
			System.out.println("Unanticipated mono mixdown channel");
			return (-1);
		}

		/* stereo mixdown elements */
		elp = pcp.stereo_mix;
		if (elp.num_ele > 0) {
			System.out.println("Unanticipated stereo mixdown channel");
			return (-1);
		}

		/* save to check future consistency */
		check_mc_info(decoder.mc_info, true);

		return 1;
	}

	/*
	 * translate prog config or default config into to multi-channel info
	 * structure returns index of channel in MC_Info
	 */
	int enter_chn(boolean cpe, int tag, char position, boolean common_window,
			MC_Info mip) throws IOException {
		int nch, cidx;
		Ch_Info cip;

		nch = cpe ? 2 : 1;

		switch (position) {
		/* use configuration already in MC_Info */
		case 0:
			cidx = ch_index(mip, cpe, tag);
			break;
		/* build configuration */
		case 'f':
			if ((mip.nfch + nch) > Constants.FChans) {
				System.out.println("Unanticipated front channel");
				return -1;
			}
			if (mip.nfch == 0) {
				/* consider case of center channel */
				if (Constants.FCenter == 1) {
					if (cpe) {
						/* has center speaker but center channel missing */
						cidx = 0 + 1;
						mip.nfch = 1 + nch;
					} else {
						if ((mip.nfsce & 1) == 1) {
							/* has center speaker and this is center channel */
							/* odd number of leading SCE's */
							cidx = 0;
							mip.nfch = nch;
						} else {
							/* has center speaker but center channel missing */
							/* even number of leading SCE's */
							/*
							 * (Note that in implicit congiguration channel to
							 * speaker mapping may be wrong for first block
							 * while count of SCE's prior to first CPE is being
							 * make. However first block is not written so it
							 * doesn't matter. Second block will be correct.
							 */
							cidx = 0 + 1;
							mip.nfch = 1 + nch;
						}
					}
				} else {
					if (cpe) {
						/* no center speaker and center channel missing */
						cidx = 0;
						mip.nfch = nch;
					} else {
						/* no center speaker so this is left channel */
						cidx = 0;
						mip.nfch = nch;
					}
				}
			} else {
				cidx = mip.nfch;
				mip.nfch += nch;
			}
			break;
		case 's':
			if ((mip.nsch + nch) > Constants.SChans) {
				System.out.println("Unanticipated side channel");
				return -1;
			}
			cidx = Constants.FChans + mip.nsch;
			mip.nsch += nch;
			break;
		case 'b':
			if ((mip.nbch + nch) > Constants.BChans) {
				System.out.println("Unanticipated back channel");
				return -1;
			}
			cidx = Constants.FChans + Constants.SChans + mip.nbch;
			mip.nbch += nch;
			break;
		case 'l':
			if ((mip.nlch + nch) > Constants.LChans) {
				System.out.println("Unanticipated LFE channel");
				return -1;
			}
			cidx = Constants.FChans + Constants.SChans + Constants.BChans
					+ mip.nfch;
			mip.nlch += nch;
			break;
		default:
			throw new IOException("enter_chn");
		}
		mip.nch += nch;

		if (cpe == false) {
			/* SCE */
			cip = mip.ch_info[cidx];
			cip.present = true;
			cip.tag = tag;
			cip.cpe = false;
			cip.common_window = common_window;
			cip.widx = cidx;
			mip.nch = cidx + 1;
		} else {
			/* CPE */
			/* left */
			cip = mip.ch_info[cidx];
			cip.present = true;
			cip.tag = tag;
			cip.cpe = true;
			cip.common_window = common_window;
			cip.ch_is_left = true;
			cip.paired_ch = cidx + 1;
			/* right */
			cip = mip.ch_info[cidx + 1];
			cip.present = true;
			cip.tag = tag;
			cip.cpe = true;
			cip.common_window = common_window;
			cip.ch_is_left = false;
			cip.paired_ch = cidx;
			if (common_window) {
				mip.ch_info[cidx].widx = cidx; /* window info is left */
				mip.ch_info[cidx + 1].widx = cidx;
			} else {
				mip.ch_info[cidx].widx = cidx; /* each has window info */
				mip.ch_info[cidx + 1].widx = cidx + 1;
			}
			mip.nch = cidx + 2;
		}
		return cidx;
	}

	char default_position(MC_Info mip, int id) {
		boolean first_cpe = false;

		if (mip.nch < Constants.FChans) {
			if (id == Constants.ID_CPE) {
				/* here is the first CPE */
				first_cpe = true;
			} else if ((decoder.bno == 0) && !first_cpe) {
				/* count number of SCE prior to first CPE in first block */
				mip.nfsce++;
			}
			return ('f');
		} else if (mip.nch < (Constants.FChans + Constants.SChans)) {
			return ('s');
		} else if (mip.nch < (Constants.FChans + Constants.SChans + Constants.BChans)) {
			return ('b');
		}

		return 0;
	}

	/*
	 * retrieve appropriate channel index for the program and decoder
	 * configuration
	 */
	int chn_config(int id, int tag, boolean common_window, MC_Info mip)
			throws IOException {
		int cidx = 0;
		char position;
		boolean cpe;

		/*
		 * channel index to position mapping for 5.1 configuration is 0 center 1
		 * left front 2 right front 3 left surround 4 right surround 5 lfe
		 */

		cpe = (id == Constants.ID_CPE) ? true : false;

		if (decoder.default_config) {
			switch (id) {
			case Constants.ID_SCE:
			case Constants.ID_CPE:
				if ((position = default_position(mip, id)) == 0) {
					throw new IOException("Unanticipated channel");
				}
				cidx = enter_chn(cpe, tag, position, common_window, mip);
				break;
			case Constants.ID_LFE:
				cidx = enter_chn(cpe, tag, 'l', common_window, mip);
				break;
			}
		} else {
			cidx = enter_chn(cpe, tag, (char) 0, common_window, mip);
		}

		return cidx; /* index of chn in mc_info */
	}

	/*
	 * check continuity of configuration from one block to next
	 */
	void reset_mc_info(MC_Info mip) {
		int i;
		Ch_Info p = new Ch_Info();

		if (decoder.default_config) {
			/* reset channel counts */
			mip.nch = 0;
			mip.nfch = 0;
			mip.nsch = 0;
			mip.nbch = 0;
			mip.nlch = 0;
			mip.ncch = 0;
			if (decoder.bno == 0) {
				/* reset prior to first block scan only! */
				mip.nfsce = 0;
			}
		}
		for (i = 0; i < Constants.Chans; i++) {
			p = mip.ch_info[i];
			p.present = false;
			if (decoder.default_config) {
				p.cpe = false;
				p.ch_is_left = false;
				p.paired_ch = 0;
				p.is_info.is_present = false;
				p.widx = 0;
				p.ncch = 0;
			}
		}
	}

	private MC_Info save_mc_info = new MC_Info();

	void check_mc_info(MC_Info mip, boolean new_config) throws IOException {
		int i, nch;
		Ch_Info s, p;
		boolean err = false;

		nch = mip.nch;
		if (new_config) {
			/* enter valid configuration */
			for (i = 0; i < nch; i++) {
				s = save_mc_info.ch_info[i];
				p = mip.ch_info[i];
				s.present = p.present;
				s.cpe = p.cpe;
				s.ch_is_left = p.ch_is_left;
				s.paired_ch = p.paired_ch;
			}
		} else {
			/* check this block's configuration */
			for (i = 0; i < nch; i++) {
				s = save_mc_info.ch_info[i];
				p = mip.ch_info[i];
				if (s.present != p.present) {
					err = true;
				}
				if (s.cpe != p.cpe) {
					err = true;
				}
				if (s.ch_is_left != p.ch_is_left) {
					err = true;
				}
				if (s.paired_ch != p.paired_ch) {
					err = true;
				}
			}
			if (err) {
				for (i = 0; i < nch; i++) {
					s = save_mc_info.ch_info[i];
					p = mip.ch_info[i];
					System.out.println("Channel " + i);
					System.out.println("present    " + s.present + " "
							+ p.present);
					System.out.println("cpe        " + s.cpe + " " + p.cpe);
					System.out.println("ch_is_left " + s.ch_is_left + " "
							+ p.ch_is_left);
					System.out.println("paired_ch  " + s.paired_ch + " "
							+ p.paired_ch);
					System.out.println();
				}
				throw new IOException("Channel configuration inconsistency");
			}
		}
	}

	/**
	 * Returns channel index of SCE or left chn in CPE using given cpe and tag.
	 * 
	 * @param mip
	 * @param cpe
	 * @param tag
	 * @return channel index of SCE or left chn in CPE
	 */
	int ch_index(MC_Info mip, boolean cpe, int tag) {
		int ch;

		for (ch = 0; ch < mip.nch; ch++) {
			if (!mip.ch_info[ch].present)
				continue;
			if ((mip.ch_info[ch].cpe == cpe) && (mip.ch_info[ch].tag == tag)) {
				return ch;
			}
		}

		/*
		 * no match, so channel is not in this program dummy up the ch_info
		 * structure so rest of chn will parse
		 */
		if (Constants.XChans > 0) {
			ch = Constants.Chans - Constants.XChans; /* left scratch channel */
			mip.ch_info[ch].cpe = cpe;
			mip.ch_info[ch].ch_is_left = true;
			mip.ch_info[ch].widx = ch;
			if (cpe) {
				mip.ch_info[ch].paired_ch = ch + 1;
				mip.ch_info[ch + 1].ch_is_left = false;
				mip.ch_info[ch + 1].paired_ch = ch;
			}
		} else {
			ch = -1; /* error, no scratch space */
		}

		return ch;
	}

	public Huffman getHuffman() {
		return huffman;
	}

	public void setHuffman(Huffman huffman) {
		this.huffman = huffman;
	}

	public BitStream getAudio_stream() {
		return audio_stream;
	}

	public void setAudio_stream(BitStream audio_stream) {
		this.audio_stream = audio_stream;
	}

	public byte getAudioObjectType() {
		return audioObjectType;
	}

	public void setAudioObjectType(byte audioObjectType) {
		this.audioObjectType = audioObjectType;
	}

	public int getChannelConfiguration() {
		return channelConfiguration;
	}

	public void setChannelConfiguration(int channelConfiguration) {
		this.channelConfiguration = channelConfiguration;
	}

	public int getCoreCoderDelay() {
		return coreCoderDelay;
	}

	public void setCoreCoderDelay(int coreCoderDelay) {
		this.coreCoderDelay = coreCoderDelay;
	}

	public AACDecoder getDecoder() {
		return decoder;
	}

	public void setDecoder(AACDecoder decoder) {
		this.decoder = decoder;
	}

	public boolean isDependsOnCoreCoder() {
		return dependsOnCoreCoder;
	}

	public void setDependsOnCoreCoder(boolean dependsOnCoreCoder) {
		this.dependsOnCoreCoder = dependsOnCoreCoder;
	}

	public boolean isExtensionFlag() {
		return extensionFlag;
	}

	public void setExtensionFlag(boolean extensionFlag) {
		this.extensionFlag = extensionFlag;
	}

	public boolean isExtensionFlag3() {
		return extensionFlag3;
	}

	public void setExtensionFlag3(boolean extensionFlag3) {
		this.extensionFlag3 = extensionFlag3;
	}

	public int getFrameLength() {
		return frameLength;
	}

	public void setFrameLength(int frameLength) {
		this.frameLength = frameLength;
	}

	public boolean isFrameLengthFlag() {
		return frameLengthFlag;
	}

	public void setFrameLengthFlag(boolean frameLengthFlag) {
		this.frameLengthFlag = frameLengthFlag;
	}

	public int getSampleFrequency() {
		return sampleFrequency;
	}

	public void setSampleFrequency(int sampleFrequency) {
		this.sampleFrequency = sampleFrequency;
	}

	public byte getSampleFrequencyIndex() {
		return sampleFrequencyIndex;
	}

	public void setSampleFrequencyIndex(byte sampleFrequencyIndex) {
		this.sampleFrequencyIndex = sampleFrequencyIndex;
	}

	public MC_Info getSave_mc_info() {
		return save_mc_info;
	}

	public void setSave_mc_info(MC_Info save_mc_info) {
		this.save_mc_info = save_mc_info;
	}
}
