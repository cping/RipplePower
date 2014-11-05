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

/**
	This software module was originally developed by Apple Computer, Inc.
	in the course of development of MPEG-4. 
	This software module is an implementation of a part of one or 
	more MPEG-4 tools as specified by MPEG-4. 
	ISO/IEC gives users of MPEG-4 free license to this
	software module or modifications thereof for use in hardware 
	or software products claiming conformance to MPEG-4.
	Those intending to use this software module in hardware or software
	products are advised that its use may infringe existing patents.
	The original developer of this software module and his/her company,
	the subsequent editors and their companies, and ISO/IEC have no
	liability for use of this software module or modifications thereof
	in an implementation.
	Copyright is not released for non MPEG-4 conforming
	products. Apple Computer, Inc. retains full right to use the code for its own
	purpose, assign or donate the code to a third party and to
	inhibit third parties from using the code for non 
	MPEG-4 conforming products.
	This copyright notice must be included in all copies or
	derivative works. Copyright (c) 1999.
*/
package mediaframe.mpeg4.isofile;


import java.util.Vector;
import java.util.Date;

import java.io.IOException;


import org.ripple.power.sound.DataStream;


/**
 * The <code>MP4Atom</code> object represents the smallest information block 
 * of the MP4 file. It could contain other atoms as children.
 */
public class MP4Atom {

	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4AudioSampleEntryAtomType 			= MP4Atom.typeToInt("mp4a");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4ChunkLargeOffsetAtomType 			= MP4Atom.typeToInt("co64");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4ChunkOffsetAtomType 					= MP4Atom.typeToInt("stco");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4DataInformationAtomType           	= MP4Atom.typeToInt("dinf");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4ESDAtomType                       	= MP4Atom.typeToInt("esds");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4ExtendedAtomType                  	= MP4Atom.typeToInt("uuid");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4HandlerAtomType                   	= MP4Atom.typeToInt("hdlr");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4MediaAtomType                     	= MP4Atom.typeToInt("mdia");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4MediaHeaderAtomType               	= MP4Atom.typeToInt("mdhd");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4MediaInformationAtomType          	= MP4Atom.typeToInt("minf");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4MovieAtomType                     	= MP4Atom.typeToInt("moov");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4MovieHeaderAtomType               	= MP4Atom.typeToInt("mvhd");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4SampleDescriptionAtomType         	= MP4Atom.typeToInt("stsd");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4SampleSizeAtomType                	= MP4Atom.typeToInt("stsz");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4CompactSampleSizeAtomType         	= MP4Atom.typeToInt("stz2");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4SampleTableAtomType               	= MP4Atom.typeToInt("stbl");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4SampleToChunkAtomType             	= MP4Atom.typeToInt("stsc");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4SoundMediaHeaderAtomType         	= MP4Atom.typeToInt("smhd");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4TrackAtomType                    	= MP4Atom.typeToInt("trak");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4TrackHeaderAtomType              	= MP4Atom.typeToInt("tkhd");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4VideoMediaHeaderAtomType         	= MP4Atom.typeToInt("vmhd");
	/** Constanta, the type of the MP4 Atom. */
	public final static int MP4VisualSampleEntryAtomType        	= MP4Atom.typeToInt("mp4v");
	
	/** The size of the atom. */
	protected long size;
	/** The type of the atom. */
	protected int type;
	/** The user's extended type of the atom. */
	protected String uuid;
	/** The amount of bytes that readed from the mpeg stream. */
	protected long readed;  
	/** The childrend of this atom. */	
	protected Vector<MP4Atom> children = new Vector<MP4Atom>();
	
	public MP4Atom(long size, int type, String uuid, long readed) {
		super();
		this.size = size;
		this.type = type;
		this.uuid = uuid;
		this.readed = readed;
	}
	
	/**
	 * Constructs an <code>Atom</code> object from the data in the bitstream.
	 * @param bitstream the input bitstream
	 * @return the constructed atom.
	 */	
	public static MP4Atom createAtom(DataStream bitstream) throws IOException {
		String uuid = null;
		long size = bitstream.readBytes(4);
		if(size == 0) {
			throw new IOException("Invalid size");
		}
		int type = (int)bitstream.readBytes(4);
		long readed = 8;
		if(type == MP4ExtendedAtomType) {
			uuid = bitstream.readString(16);
			readed += 16;
		}
		// large size
		if(size == 1) {
			size = bitstream.readBytes(8);
			readed += 8;
		}
		MP4Atom atom = new MP4Atom(size, type, uuid, readed);
		if((type == MP4MediaAtomType) || (type == MP4DataInformationAtomType) || (type == MP4MovieAtomType)
			|| (type == MP4MediaInformationAtomType) || (type == MP4SampleTableAtomType) || (type == MP4TrackAtomType)) {
			readed = atom.create_composite_atom(bitstream);
		} else if(type == MP4AudioSampleEntryAtomType) {
			readed = atom.create_audio_sample_entry_atom(bitstream);
		} else if(type == MP4ChunkLargeOffsetAtomType) {
			readed = atom.create_chunk_large_offset_atom(bitstream);
		} else if(type == MP4ChunkOffsetAtomType) {
			readed = atom.create_chunk_offset_atom(bitstream);
		} else if(type == MP4HandlerAtomType){
			readed = atom.create_handler_atom(bitstream);
		} else if(type == MP4MediaHeaderAtomType){
			readed = atom.create_media_header_atom(bitstream);
		} else if(type == MP4MovieHeaderAtomType){
			readed = atom.create_movie_header_atom(bitstream);
		} else if(type == MP4SampleDescriptionAtomType){
			readed = atom.create_sample_description_atom(bitstream);
		} else if(type == MP4SampleSizeAtomType){
			readed = atom.create_sample_size_atom(bitstream);
		} else if(type == MP4CompactSampleSizeAtomType){
			readed = atom.create_compact_sample_size_atom(bitstream);
		} else if(type == MP4SampleToChunkAtomType){
			readed = atom.create_sample_to_chunk_atom(bitstream);
/*			
		} else if(type == MP4SoundMediaHeaderAtomType){
			readed = atom.create_sound_media_header_atom(bitstream);
		} else if(type == MP4TrackHeaderAtomType){
			readed = atom.create_track_header_atom(bitstream);
		} else if(type == MP4VideoMediaHeaderAtomType){
			readed = atom.create_video_media_header_atom(bitstream);
*/			
		} else if(type == MP4VisualSampleEntryAtomType){
			readed = atom.create_visual_sample_entry_atom(bitstream);
		} else if(type == MP4ESDAtomType) {
			readed = atom.create_esd_atom(bitstream);
		}
						
//		System.out.println("Atom: type = " + intToType(type) + " size = " + size);
		bitstream.skipBytes(size - readed);
		return atom;
	}	

	protected int version = 0;
	protected int flags = 0;
	
	/**
	 * Loads the version of the full atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_full_atom(DataStream bitstream) throws IOException {
		long value = bitstream.readBytes(4);
		version = (int)value >> 24;
		flags = (int)value & 0xffffff;
		readed += 4;
		return readed;		
	}

	/**
	 * Loads the composite atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_composite_atom(DataStream bitstream) throws IOException {
		while(readed < size) {
			MP4Atom child = MP4Atom.createAtom(bitstream);
			this.children.addElement(child);
			readed += child.getSize();
		}
		return readed;		
	}
	
	/**
	 * Lookups for a child atom with the specified <code>type</code>, skips the <code>number</code> 
	 * children with the same type before finding a result. 
	 * @param type the type of the atom.
	 * @param number the number of atoms to skip
	 * @return the atom which was being searched. 
	 */	
	public MP4Atom lookup(long type, int number) {
		int position = 0; 
		for(int i = 0; i < children.size(); i++) {
			MP4Atom atom = (MP4Atom)children.elementAt(i);
			if(atom.getType() == type) {
				if(position >= number) {
					return atom;
				}
				position ++;
			}
		}
		return null;
	}

	
	/**
	 * Loads AudioSampleEntry atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_audio_sample_entry_atom(DataStream bitstream) throws IOException {
		bitstream.skipBytes(6);
		 bitstream.readBytes(2);
		bitstream.skipBytes(8);
		bitstream.readBytes(2);
		 bitstream.readBytes(2);
		bitstream.skipBytes(4);
		 bitstream.readBytes(2);
		bitstream.skipBytes(2);
		readed += 28;
		MP4Atom child = MP4Atom.createAtom(bitstream);
		this.children.addElement(child);
		readed += child.getSize();
		return readed;		
	}
	
	protected int entryCount;
	
	/** The decoding time to sample table. */
	protected Vector<Long> chunks = new Vector<Long>();

	/**
	 * Loads ChunkLargeOffset atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_chunk_large_offset_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		entryCount = (int)bitstream.readBytes(4);
		readed += 8;
		for(int i = 0; i < entryCount; i++) {
			long chunkOffset = bitstream.readBytes(8);
			chunks.addElement(new Long(chunkOffset));
			readed += 8;
		}
		return readed;		
	}

	public Vector<Long> getChunks() {
		return chunks;
	}

	/**
	 * Loads ChunkOffset atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_chunk_offset_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		entryCount = (int)bitstream.readBytes(4);
		readed += 4;
		for(int i = 0; i < entryCount; i++) {
			long chunkOffset = bitstream.readBytes(4);
			chunks.addElement(new Long(chunkOffset));
			readed += 4;
		}
		return readed;		
	}

	protected int handlerType;

	/**
	 * Loads Handler atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_handler_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		bitstream.readBytes(4);
		handlerType = (int)bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		readed += 20;
		int length = (int) (size - readed - 1);
		bitstream.readString(length);
		readed += length;		
		return readed;		
	}

	/**
	 * Gets the handler type.
	 * @return the handler type.
	 */	
	public int getHandlerType() {
		return handlerType;
	}

	protected Date creationTime;
	protected Date modificationTime;
	protected int timeScale;
	protected long duration;

	/**
	 * Loads MediaHeader atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_media_header_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		if(version == 1) {
			creationTime = createDate(bitstream.readBytes(8));
			modificationTime = createDate(bitstream.readBytes(8));
			timeScale = (int)bitstream.readBytes(4);
			duration = bitstream.readBytes(8);
			readed += 28;
		} else {
			creationTime = createDate(bitstream.readBytes(4));
			modificationTime = createDate(bitstream.readBytes(4));
			timeScale = (int)bitstream.readBytes(4);
			duration = bitstream.readBytes(4);
			readed += 16;
		}
		bitstream.readBytes(2);
		bitstream.readBytes(2);
		readed += 4; 
		return readed;		
	}
	
	public long getDuration() {
		return duration;
	}

	public int getTimeScale() {
		return timeScale;
	}

	/**
	 * Loads MovieHeader atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_movie_header_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		if(version == 1) {
			creationTime = createDate(bitstream.readBytes(8));
			modificationTime = createDate(bitstream.readBytes(8));
			timeScale = (int)bitstream.readBytes(4);
			duration = bitstream.readBytes(8);
			readed += 28;
		} else {
			creationTime = createDate(bitstream.readBytes(4));
			modificationTime = createDate(bitstream.readBytes(4));
			timeScale = (int)bitstream.readBytes(4);
			duration = bitstream.readBytes(4);
			readed += 16;
		}
		bitstream.readBytes(4);
		bitstream.readBytes(2);
		bitstream.skipBytes(10);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		bitstream.readBytes(4);
		readed += 80; 	
		return readed;		
	}

	/**
	 * Loads SampleDescription atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_sample_description_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		entryCount = (int)bitstream.readBytes(4);
		readed += 4;
		for(int i = 0; i < entryCount; i++) {
			MP4Atom child = MP4Atom.createAtom(bitstream);
			this.children.addElement(child);
			readed += child.getSize();
		}
		return readed;		
	}

	protected int sampleSize;
	protected int sampleCount;
	
	/** The decoding time to sample table. */
	protected Vector<Integer> samples = new Vector<Integer>();

	/**
	 * Loads MP4SampleSizeAtom atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_sample_size_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		sampleSize = (int)bitstream.readBytes(4);
		sampleCount = (int)bitstream.readBytes(4);
		readed += 8;
		if(sampleSize == 0) {
			for(int i = 0; i < sampleCount; i++) {
				int size = (int)bitstream.readBytes(4);
				samples.addElement(new Integer(size));
				readed += 4;
			}
		}
		return readed;
	}

	public Vector<Integer> getSamples() {
		return samples;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	protected int fieldSize;
	
	/**
	 * Loads CompactSampleSize atom from the input stream.
	 * @param stream the input stream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_compact_sample_size_atom(DataStream stream) throws IOException {
		create_full_atom(stream);
		stream.skipBytes(3);
		sampleSize = 0;
		fieldSize = (int)stream.readBytes(1);
		sampleCount = (int)stream.readBytes(4);
		readed += 8;
		for(int i = 0; i < sampleCount; i++) {
			int size = 0;
			switch(fieldSize) {
				case 4:
					size = (int)stream.readBytes(1);
					// TODO check the following code
					samples.addElement(new Integer(size & 0x0f));
					size = (size >> 4) & 0x0f;
					i++;
					readed += 1;
					break;
				case 8:
					size = (int)stream.readBytes(1);
					readed += 1;
					break;
				case 16:
					size = (int)stream.readBytes(2);
					readed += 2;
					break;
			}
			if(i < sampleCount) {
				samples.addElement(new Integer(size));
			}
		}
		return readed;		
	}

	public class Record {
		private int firstChunk;
		private int samplesPerChunk;
		private int sampleDescriptionIndex;
		
		public Record(int firstChunk, int samplesPerChunk, int sampleDescriptionIndex) {
			this.firstChunk = firstChunk;
			this.samplesPerChunk = samplesPerChunk;
			this.sampleDescriptionIndex = sampleDescriptionIndex;
		}
		
		public int getFirstChunk() {
			return firstChunk;
		}
		public int getSamplesPerChunk(){
			return samplesPerChunk;
		}
		public int getSampleDescriptionIndex(){
			return sampleDescriptionIndex;
		}
	}

	/** The decoding time to sample table. */
	protected Vector<Record> records = new Vector<Record>();

	public Vector<Record> getRecords() {
		return records;
	}

	/**
	 * Loads MP4SampleToChunkAtom atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_sample_to_chunk_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		entryCount = (int)bitstream.readBytes(4);
		readed += 4;
		for(int i = 0; i < entryCount; i++) {
			int firstChunk = (int)bitstream.readBytes(4);
			int samplesPerChunk = (int)bitstream.readBytes(4);
			int sampleDescriptionIndex = (int)bitstream.readBytes(4);
			records.addElement(new Record(firstChunk, samplesPerChunk, sampleDescriptionIndex));
			readed += 12;
		}
		return readed;		
	}

/*
	protected int balance;

	/**
	 * Loads MP4SoundMediaHeaderAtom atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 /
	public long create_sound_media_header_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		balance = (int)bitstream.readBytes(2);
		bitstream.skipBytes(2);
		readed += 4;
		return readed;		
	}

	protected long trackId;
	protected int qt_trackWidth;
	protected int qt_trackHeight;
	
	/**
	 * Loads MP4TrackHeaderAtom atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 /
	public long create_track_header_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		if(version == 1) {
			creationTime = createDate(bitstream.readBytes(8));
			modificationTime = createDate(bitstream.readBytes(8));
			trackId = bitstream.readBytes(4);
			bitstream.skipBytes(4);
			duration = bitstream.readBytes(8);
			readed += 32;
		} else {
			creationTime = createDate(bitstream.readBytes(4));
			modificationTime = createDate(bitstream.readBytes(4));
			trackId = bitstream.readBytes(4);
			bitstream.skipBytes(4);
			duration = bitstream.readBytes(4);
			readed += 20;
		}
		bitstream.skipBytes(8);
		int qt_layer = (int)bitstream.readBytes(2);
		int qt_alternateGroup = (int)bitstream.readBytes(2);
		int qt_volume = (int)bitstream.readBytes(2);
		bitstream.skipBytes(2);
		long qt_matrixA = bitstream.readBytes(4);
		long qt_matrixB = bitstream.readBytes(4);
		long qt_matrixU = bitstream.readBytes(4);
		long qt_matrixC = bitstream.readBytes(4);
		long qt_matrixD = bitstream.readBytes(4);
		long qt_matrixV = bitstream.readBytes(4);
		long qt_matrixX = bitstream.readBytes(4);
		long qt_matrixY = bitstream.readBytes(4);
		long qt_matrixW = bitstream.readBytes(4);
		qt_trackWidth = (int)bitstream.readBytes(4);
		qt_trackHeight = (int)bitstream.readBytes(4);
		readed += 60; 	
		return readed;		
	}
	
	protected int graphicsMode;
	protected int opColorRed;
	protected int opColorGreen;
	protected int opColorBlue;

	/**
	 * Loads MP4VideoMediaHeaderAtom atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 /
	public long create_video_media_header_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		if((size - readed) == 8) {
			graphicsMode = (int)bitstream.readBytes(2);
			opColorRed = (int)bitstream.readBytes(2);
			opColorGreen = (int)bitstream.readBytes(2);
			opColorBlue = (int)bitstream.readBytes(2);
			readed += 8;		
		}
		return readed;		
	}
*/
	protected int width;
	protected int height;

	/**
	 * Loads MP4VisualSampleEntryAtom atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_visual_sample_entry_atom(DataStream bitstream) throws IOException {
		bitstream.skipBytes(24);
		width = (int)bitstream.readBytes(2);
		height = (int)bitstream.readBytes(2);
		bitstream.skipBytes(50);
		readed += 78;		
		MP4Atom child = MP4Atom.createAtom(bitstream);
		this.children.addElement(child);
		readed += child.getSize();
		return readed;		
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	protected MP4Descriptor esd_descriptor;
	
	/**
	 * Loads M4ESDAtom atom from the input bitstream.
	 * @param bitstream the input bitstream
	 * @return the number of bytes which was being loaded.
	 */
	public long create_esd_atom(DataStream bitstream) throws IOException {
		create_full_atom(bitstream);
		esd_descriptor = MP4Descriptor.createDescriptor(bitstream);
		readed += esd_descriptor.getReaded(); 
		return readed;		
	}

	/**
	 * Returns the ESD descriptor.
	 */
	public MP4Descriptor getEsd_descriptor() {
		return esd_descriptor;
	}

	/**
	 * Converts the time in seconds since midnight 1 Jan 1904 to the <code>Date</code>.  
	 * @param movieTime the time in milliseconds since midnight 1 Jan 1904.
	 * @return the <code>Date</code> object.
	 */
	public static final Date createDate(long movieTime) {
		return new Date(movieTime * 1000 - 2082850791998L); 
	}
	
	public static int typeToInt(String type) {
		int result = ((int)type.charAt(0) << 24) + ((int)type.charAt(1) << 16) + ((int)type.charAt(2) << 8) + ((int)type.charAt(3));
		return result;
	}
	
	public static String intToType(int type) {
		StringBuffer st = new StringBuffer();
		st.append((char)((type >> 24) & 0xff)); 
		st.append((char)((type >> 16) & 0xff)); 
		st.append((char)((type >> 8) & 0xff)); 
		st.append((char)(type & 0xff)); 
		return st.toString();
	}
	
	/**
	 * Gets children from this atom.
	 * @return children from this atom.
	 */
	public Vector<MP4Atom> getChildren() {
		return children;
	}

	/**
	 * Gets the size of this atom.
	 * @return the size of this atom.
	 */
	public long getSize() {
		return size;
	}
	
	/**
	 * Returns the type of this atom.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the name of this atom.
	 */
	public String toString() {
		return intToType(type);
	}

}
