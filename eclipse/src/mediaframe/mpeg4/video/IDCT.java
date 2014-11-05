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
package mediaframe.mpeg4.video;

/************************************************************************
 *
 *  idct.c, inverse fast DCT for tmndecode (H.263 decoder)
 *  Copyright (C) 1995, 1996  Telenor R&D, Norway
 *        Karl Olav Lillevold <Karl.Lillevold@nta.no>
 *  
 *  Contacts: 
 *  Karl Olav Lillevold               <Karl.Lillevold@nta.no>, or
 *  Robert Danielsen                  <Robert.Danielsen@nta.no>
 *
 *  Telenor Research and Development  http://www.nta.no/brukere/DVC/
 *  P.O.Box 83                        tel.:   +47 63 84 84 00
 *  N-2007 Kjeller, Norway            fax.:   +47 63 81 00 76
 *  
 ************************************************************************/

/*
 * Disclaimer of Warranty
 *
 * These software programs are available to the user without any license
 * fee or royalty on an "as is" basis.  Telenor Research and Development
 * disclaims any and all warranties, whether express, implied, or statuary,
 * including any implied warranties or merchantability or of fitness for a
 * particular purpose.  In no event shall the copyright-holder be liable
 * for any incidental, punitive, or consequential damages of any kind
 * whatsoever arising from the use of these programs.
 *
 * This disclaimer of warranty extends to the user of these programs and
 * user's customers, employees, agents, transferees, successors, and
 * assigns.
 *
 * Telenor Research and Development does not represent or warrant that the
 * programs furnished hereunder are free of infringement of any third-party
 * patents.
 *
 * Commercial implementations of H.263, including shareware, are subject to
 * royalty fees to patent holders.  Many of these patents are general
 * enough such that they are unavoidable regardless of implementation
 * design.
 *
 */


/*
 * based on mpeg2decode, (C) 1994, MPEG Software Simulation Group
 * and mpeg2play, (C) 1994 Stefan Eckart
 *                         <stefan@lis.e-technik.tu-muenchen.de>
 *
 */


/**********************************************************
/* inverse two dimensional DCT, Chen-Wang algorithm       *
/* (cf. IEEE ASSP-32, pp. 803-816, Aug. 1984)             *
/* 32-bit integer arithmetic (8 bit coefficients)         *
/* 11 mults, 29 adds per DCT                              *
/*                                      sE, 18.8.91       *
/**********************************************************
/* coefficients extended to 12 bit for IEEE1180-1990      *
/* compliance                           sE,  2.1.94       *
/**********************************************************/

/**
 * IDCT
 * 
 */
public final class IDCT {


	/* this code assumes >> to be a two's-complement arithmetic */
	/* right shift: (-2)>>1 == -1 , (-3)>>1 == -2               */

	private final static int W1 = 2841; /* 2048*sqrt(2)*cos(1*pi/16) */
	private final static int W2 = 2676; /* 2048*sqrt(2)*cos(2*pi/16) */
	private final static int W3 = 2408; /* 2048*sqrt(2)*cos(3*pi/16) */
	private final static int W5 = 1609; /* 2048*sqrt(2)*cos(5*pi/16) */
	private final static int W6 = 1108; /* 2048*sqrt(2)*cos(6*pi/16) */
	private final static int W7 = 565;  /* 2048*sqrt(2)*cos(7*pi/16) */

	private int pixels_per_line;
	
	private int line_1_index; 
	private int line_2_index; 
	private int line_3_index; 
	private int line_4_index; 
	private int line_5_index; 
	private int line_6_index; 
	private int line_7_index; 

	public IDCT(int pixels_per_line) {
		super();
		line_1_index = 1 * pixels_per_line; 
		line_2_index = 2 * pixels_per_line; 
		line_3_index = 3 * pixels_per_line; 
		line_4_index = 4 * pixels_per_line; 
		line_5_index = 5 * pixels_per_line; 
		line_6_index = 6 * pixels_per_line; 
		line_7_index = 7 * pixels_per_line;
		this.pixels_per_line = pixels_per_line;
	}

	int x0, x1, x2, x3, x4, x5, x6, x7, x8;

	/* row (horizontal) IDCT
	 *
	 *           7                       pi         1
	 * dst[k] = sum c[l] * src[l] * cos( -- * ( k + - ) * l )
	 *          l=0                      8          2
	 *
	 * where: c[0]    = 128
	 *        c[1..7] = 128*sqrt(2)
	 */

	private void idctrow(int[] blk, int[] pixel_data, int block_pointer)
	{
	  /* shortcut */
	  if (((x1 = blk[4]<<11) | (x2 = blk[6]) | (x3 = blk[2]) |
			(x4 = blk[1]) | (x5 = blk[7]) | (x6 = blk[5]) | (x7 = blk[3])) == 0)
	  {
		pixel_data[block_pointer]=pixel_data[block_pointer + 1]=
			pixel_data[block_pointer + 2]=pixel_data[block_pointer + 3]=
			pixel_data[block_pointer + 4]=pixel_data[block_pointer + 5]=
			pixel_data[block_pointer + 6]=pixel_data[block_pointer + 7]=blk[0]<<3;
		return;
	  }

	  x0 = (blk[0]<<11) + 128; /* for proper rounding in the fourth stage */

	  /* first stage */
	  x8 = W7*(x4+x5);
	  x4 = x8 + (W1-W7)*x4;
	  x5 = x8 - (W1+W7)*x5;
	  x8 = W3*(x6+x7);
	  x6 = x8 - (W3-W5)*x6;
	  x7 = x8 - (W3+W5)*x7;
  
	  /* second stage */
	  x8 = x0 + x1;
	  x0 -= x1;
	  x1 = W6*(x3+x2);
	  x2 = x1 - (W2+W6)*x2;
	  x3 = x1 + (W2-W6)*x3;
	  x1 = x4 + x6;
	  x4 -= x6;
	  x6 = x5 + x7;
	  x5 -= x7;
  
	  /* third stage */
	  x7 = x8 + x3;
	  x8 -= x3;
	  x3 = x0 + x2;
	  x0 -= x2;
	  x2 = (181*(x4+x5)+128)>>8;
	  x4 = (181*(x4-x5)+128)>>8;
  
	  /* fourth stage */
	  pixel_data[block_pointer] = (x7+x1)>>8;
	  pixel_data[block_pointer + 1] = (x3+x2)>>8;
	  pixel_data[block_pointer + 2] = (x0+x4)>>8;
	  pixel_data[block_pointer + 3] = (x8+x6)>>8;
	  pixel_data[block_pointer + 4] = (x8-x6)>>8;
	  pixel_data[block_pointer + 5] = (x0-x4)>>8;
	  pixel_data[block_pointer + 6] = (x3-x2)>>8;
	  pixel_data[block_pointer + 7] = (x7-x1)>>8;
	}

	/* column (vertical) IDCT
	 *
	 *             7                         pi         1
	 * dst[8*k] = sum c[l] * src[8*l] * cos( -- * ( k + - ) * l )
	 *            l=0                        8          2
	 *
	 * where: c[0]    = 1/1024
	 *        c[1..7] = (1/1024)*sqrt(2)
	 */
	private void idctcol(int[] pixel_data, int block_pointer)
	{
	  /* shortcut */
	  if (((x1 = (pixel_data[block_pointer + line_4_index]<<8)) | (x2 = pixel_data[block_pointer + line_6_index]) 
	  		| (x3 = pixel_data[block_pointer + line_2_index]) |	(x4 = pixel_data[block_pointer + line_1_index]) 
	  		| (x5 = pixel_data[block_pointer + line_7_index]) | (x6 = pixel_data[block_pointer + line_5_index]) 
	  		| (x7 = pixel_data[block_pointer + line_3_index])) == 0)
	  {
		pixel_data[block_pointer]=pixel_data[block_pointer + line_1_index]=
			pixel_data[block_pointer + line_2_index]=pixel_data[block_pointer + line_3_index] =
			pixel_data[block_pointer + line_4_index]=pixel_data[block_pointer + line_5_index] =
			pixel_data[block_pointer + line_6_index]=pixel_data[block_pointer + line_7_index] = 
				((pixel_data[block_pointer]+32)>>6);
		return;
	  }

	  x0 = (pixel_data[block_pointer]<<8) + 8192;

	  /* first stage */
	  x8 = W7*(x4+x5) + 4;
	  x4 = (x8+(W1-W7)*x4)>>3;
	  x5 = (x8-(W1+W7)*x5)>>3;
	  x8 = W3*(x6+x7) + 4;
	  x6 = (x8-(W3-W5)*x6)>>3;
	  x7 = (x8-(W3+W5)*x7)>>3;
  
	  /* second stage */
	  x8 = x0 + x1;
	  x0 -= x1;
	  x1 = W6*(x3+x2) + 4;
	  x2 = (x1-(W2+W6)*x2)>>3;
	  x3 = (x1+(W2-W6)*x3)>>3;
	  x1 = x4 + x6;
	  x4 -= x6;
	  x6 = x5 + x7;
	  x5 -= x7;
  
	  /* third stage */
	  x7 = x8 + x3;
	  x8 -= x3;
	  x3 = x0 + x2;
	  x0 -= x2;
	  x2 = (181*(x4+x5)+128)>>8;
	  x4 = (181*(x4-x5)+128)>>8;
  
	  /* fourth stage */
	  pixel_data[block_pointer] = ((x7+x1)>>14);
	  pixel_data[block_pointer + line_1_index] = ((x3+x2)>>14);
	  pixel_data[block_pointer + line_2_index] = ((x0+x4)>>14);
	  pixel_data[block_pointer + line_3_index] = ((x8+x6)>>14);
	  pixel_data[block_pointer + line_4_index] = ((x8-x6)>>14);
	  pixel_data[block_pointer + line_5_index] = ((x0-x4)>>14);
	  pixel_data[block_pointer + line_6_index] = ((x3-x2)>>14);
	  pixel_data[block_pointer + line_7_index] = ((x7-x1)>>14);
	}

	/* two dimensional inverse discrete cosine transform */
	public void idct(int[][] block, int[] pixel_data, int block_pointer)
	{
	  int i;

	  for (i=0; i<8; i++)
		idctrow(block[i], pixel_data, block_pointer + i * pixels_per_line);

	  for (i=0; i<8; i++)
		idctcol(pixel_data, block_pointer + i);
		
	}

}
