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
 * Info
 */
final class Info {
	/** true if long block */
	boolean	islong;				
	/** sub-blocks (SB) per block */
	int	    nsbk;				
	/** coef's per block */
	int	    bins_per_bk;		
	/** sfb per block */
	int	    sfb_per_bk;			
	/** coef's per SB */
	int[]	bins_per_sbk = new int[Constants.MAX_SBK];	
	/** sfb per SB */
	int[]   sfb_per_sbk = new int[Constants.MAX_SBK];	
	int[]   sectbits = new int [Constants.MAX_SBK];
	/** top coef per sfb per SB */
	short[][] sbk_sfb_top = new short [Constants.MAX_SBK][];
	/** sfb width for short blocks */	
	short[] sfb_width_128 = null;				
	/** cum version of above */
	short[] bk_sfb_top = new short[200];
	int	    num_groups;
	short[] group_len = new short[8];
	short[] group_offs = new short[8];
	
	void copyFields(Info info) {
		this.islong = info.islong;
		this.nsbk = info.nsbk;
		this.bins_per_bk = info.bins_per_bk;
		this.sfb_per_bk = info.sfb_per_bk;
		
		this.sectbits = info.sectbits;
		this.bins_per_bk = info.bins_per_bk;
		this.sfb_per_sbk = info.sfb_per_sbk;
		this.sbk_sfb_top = info.sbk_sfb_top;
		this.bk_sfb_top = info.bk_sfb_top;
		this.sfb_width_128 = info.sfb_width_128;
		this.group_len = info.group_len;
		this.group_offs = info.group_offs;  
/*		
		for(i =0; i < Constants.MAX_SBK; i++) {
			this.sectbits[i] = info.sectbits[i];
			this.bins_per_sbk[i] = info.bins_per_sbk[i];
			this.sfb_per_sbk[i] = info.sfb_per_sbk[i];
			if(info.sbk_sfb_top[i] != null) {
				this.sbk_sfb_top[i] = new short[info.sbk_sfb_top[i].length];
				for(j = 0; j < info.sbk_sfb_top[i].length; j++) {
					this.sbk_sfb_top[i][j] = info.sbk_sfb_top[i][j];
				}
			}
		}
		for(i =0; i < 200; i++) {
			this.bk_sfb_top[i] = info.bk_sfb_top[i];
		}
		if(info.sfb_width_128 != null) {
			this.sfb_width_128 = new short[info.sfb_width_128.length];
			for(j = 0; j < info.sfb_width_128.length; j++) {
				this.sfb_width_128[j] = info.sfb_width_128[j];
			}
		}
		for(i =0; i < 8; i++) {
			this.group_len[i] = info.group_len[i];
			this.group_offs[i] = info.group_offs[i];
		}
*/		
		this.num_groups = info.num_groups;
	}
}
