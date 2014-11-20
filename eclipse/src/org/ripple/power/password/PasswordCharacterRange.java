package org.ripple.power.password;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class PasswordCharacterRange {
	
	private static int[] lowerBounds = new int[0];
	private static int[] upperBounds = new int[0];
	
	private TreeSet<CharacterBlock> characterClasses;
	
	public PasswordCharacterRange(String password) {
		
		characterClasses = new TreeSet<CharacterBlock>();
		
		if(null == password) {
			return;
		}
		
		if(lowerBounds.length < 1) {
			initBoundsArrays();
		}
		
		int passwordLength = password.length();
		for(int i = 0; i < passwordLength; i++) {
			int codePoint = password.codePointAt(i);
			this.add(codePoint);
			
			if(Character.isSupplementaryCodePoint(codePoint)) {
				i++;
			}
		}
	}
	
	private void initBoundsArrays() {
		lowerBounds = new int[CharacterBlock.values().length];
		upperBounds = new int[CharacterBlock.values().length];
		
		for(CharacterBlock block : CharacterBlock.values()) {
			int index = block.ordinal();
			Iterator<Range> i = block.ranges.iterator();
			Range range = i.next();
			lowerBounds[index] = range.getLowerBound();
			while(i.hasNext()) {
				range = i.next();
			}
			upperBounds[index] = range.getUpperBound();
		}
	}

	private void add(int codePoint) {
		if(this.contains(codePoint)) {
			return;
		}
		CharacterBlock[] blocks = CharacterBlock.values();
		
		if(codePoint < 0x7f) {
			for(int i = 0; i < blocks.length; i++) {
				CharacterBlock group = blocks[i];
				if(group.contains(codePoint)) {
					this.characterClasses.add(group);
					return;
				}
			}
		} 
		else {
			int min = CharacterBlock.LATIN_EXTENDED_A.ordinal();
			int max = CharacterBlock.SUPPLEMENTARY_PRIVATE_USE_AREA_B.ordinal();
			int guess = (int) (Math.floor((max - min) / 2.0)) + min;
			while(max >= min) {
				if(upperBounds[guess] < codePoint) {
					min = guess + 1;
				} else if(lowerBounds[guess] > codePoint){
					max = guess - 1;
				} else {
					characterClasses.add(blocks[guess]);
					return;
				}
				
				guess = (int) (Math.floor((max - min) / 2.0)) + min;
			}
		}
		
		if(CharacterBlock._UNUSED.contains(codePoint)) {
			this.characterClasses.add(CharacterBlock._UNUSED);
			return;
		} 
	}

	public long size() {
		long result = 0;
		for(CharacterBlock group : characterClasses) {
			for(Range range : group.getRanges()) {
				result += (range.upperBound - range.lowerBound) + 1;
			}
		}
		return result;
	}
	
	private boolean contains(int codePoint) {
		for(CharacterBlock group : characterClasses) {
			for (Range range : group.getRanges()) {
				if (range.lowerBound <= codePoint && range.upperBound >= codePoint) {
					return true;
				}
			}
		}
		return false;
	}
	
	public long position(int codePoint) {
		long count = 0;
		for(CharacterBlock group : characterClasses) {
			for(Range range : group.getRanges()) {
				if(range.lowerBound <= codePoint && range.upperBound >= codePoint) {
					return count + (codePoint - range.lowerBound);
				} else {
					count += (range.upperBound - range.lowerBound) + 1;
				}
			}
		}
		
		return -1;
	}
	
	protected static class Range {
		private int lowerBound, upperBound;
		public Range(int lowerBound, int upperBound) {
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}
		
		public int getLowerBound() {
			return this.lowerBound;
		}
		
		public int getUpperBound() {
			return this.upperBound;
		}
	}
	
	protected static enum CharacterBlock {
		BASIC_LATIN_LETTERS_LOWER_CASE(new Range(0x61, 0x7a)),
		BASIC_LATIN_LETTERS_UPPER_CASE(new Range(0x41, 0x5a)),
		BASIC_LATIN_NUMERICAL_DIGITS(new Range(0x30,0x39)),
		
		BASIC_LATIN_SYMBOLS(
				new Range(0x20, 0x2f),
				new Range(0x3a, 0x40),
				new Range(0x5b, 0x60),
				new Range(0x7b, 0x7e)),
		
		BASIC_LATIN_CONTROL_CHARACTERS(
				new Range(0x0, 0x1f), 
				new Range(0x7f, 0x7f)),
			
		LATIN_1_SUPPLEMENT(new Range(0x0080, 0x00FF)),
		LATIN_EXTENDED_A(new Range(0x0100, 0x017F)),
		LATIN_EXTENDED_B(new Range(0x0180, 0x024F)),
		IPA_EXTENSIONS(new Range(0x0250, 0x02AF)),
		SPACING_MODIFIER_LETTERS(new Range(0x02B0, 0x02FF)),
		COMBINING_DIACRITICAL_MARKS(new Range(0x0300, 0x036F)),
		GREEK_AND_COPTIC(new Range(0x0370, 0x03FF)),
		CYRILLIC(new Range(0x0400, 0x04FF)),
		CYRILLIC_SUPPLEMENT(new Range(0x0500, 0x052F)),
		ARMENIAN(new Range(0x0530, 0x058F)),
		HEBREW(new Range(0x0590, 0x05FF)),
		ARABIC(new Range(0x0600, 0x06FF)),
		SYRIAC(new Range(0x0700, 0x074F)),
		ARABIC_SUPPLEMENT(new Range(0x0750, 0x077F)),
		THAANA(new Range(0x0780, 0x07BF)),
		NKO(new Range(0x07C0, 0x07FF)),
		SAMARITAN(new Range(0x0800, 0x083F)),
		DEVANAGARI(new Range(0x0900, 0x097F)),
		BENGALI(new Range(0x0980, 0x09FF)),
		GURMUKHI(new Range(0x0A00, 0x0A7F)),
		GUJARATI(new Range(0x0A80, 0x0AFF)),
		ORIYA(new Range(0x0B00, 0x0B7F)),
		TAMIL(new Range(0x0B80, 0x0BFF)),
		TELUGU(new Range(0x0C00, 0x0C7F)),
		KANNADA(new Range(0x0C80, 0x0CFF)),
		MALAYALAM(new Range(0x0D00, 0x0D7F)),
		SINHALA(new Range(0x0D80, 0x0DFF)),
		THAI(new Range(0x0E00, 0x0E7F)),
		LAO(new Range(0x0E80, 0x0EFF)),
		TIBETAN(new Range(0x0F00, 0x0FFF)),
		MYANMAR(new Range(0x1000, 0x109F)),
		GEORGIAN(new Range(0x10A0, 0x10FF)),
		HANGUL_JAMO(new Range(0x1100, 0x11FF)),
		ETHIOPIC(new Range(0x1200, 0x137F)),
		ETHIOPIC_SUPPLEMENT(new Range(0x1380, 0x139F)),
		CHEROKEE(new Range(0x13A0, 0x13FF)),
		UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS(new Range(0x1400, 0x167F)),
		OGHAM(new Range(0x1680, 0x169F)),
		RUNIC(new Range(0x16A0, 0x16FF)),
		TAGALOG(new Range(0x1700, 0x171F)),
		HANUNOO(new Range(0x1720, 0x173F)),
		BUHID(new Range(0x1740, 0x175F)),
		TAGBANWA(new Range(0x1760, 0x177F)),
		KHMER(new Range(0x1780, 0x17FF)),
		MONGOLIAN(new Range(0x1800, 0x18AF)),
		UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED(new Range(0x18B0, 0x18FF)),
		LIMBU(new Range(0x1900, 0x194F)),
		TAI_LE(new Range(0x1950, 0x197F)),
		NEW_TAI_LUE(new Range(0x1980, 0x19DF)),
		KHMER_SYMBOLS(new Range(0x19E0, 0x19FF)),
		BUGINESE(new Range(0x1A00, 0x1A1F)),
		TAI_THAM(new Range(0x1A20, 0x1AAF)),
		BALINESE(new Range(0x1B00, 0x1B7F)),
		SUNDANESE(new Range(0x1B80, 0x1BBF)), 
		LEPCHA(new Range(0x1C00, 0x1C4F)),
		OL_CHIKI(new Range(0x1C50, 0x1C7F)),
		VEDIC_EXTENSIONS(new Range(0x1CD0, 0x1CFF)),
		PHONETIC_EXTENSIONS(new Range(0x1D00, 0x1D7F)),
		PHONETIC_EXTENSIONS_SUPPLEMENT(new Range(0x1D80, 0x1DBF)),
		COMBINING_DIACRITICAL_MARKS_SUPPLEMENT(new Range(0x1DC0, 0x1DFF)),
		LATIN_EXTENDED_ADDITIONAL(new Range(0x1E00, 0x1EFF)),
		GREEK_EXTENDED(new Range(0x1F00, 0x1FFF)),
		GENERAL_PUNCTUATION(new Range(0x2000, 0x206F)),
		SUPERSCRIPTS_AND_SUBSCRIPTS(new Range(0x2070, 0x209F)),
		CURRENCY_SYMBOLS(new Range(0x20A0, 0x20CF)),
		COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS(new Range(0x20D0, 0x20FF)),
		LETTERLIKE_SYMBOLS(new Range(0x2100, 0x214F)),
		NUMBER_FORMS(new Range(0x2150, 0x218F)),
		ARROWS(new Range(0x2190, 0x21FF)),
		MATHEMATICAL_OPERATORS(new Range(0x2200, 0x22FF)),
		MISCELLANEOUS_TECHNICAL(new Range(0x2300, 0x23FF)),
		CONTROL_PICTURES(new Range(0x2400, 0x243F)),
		OPTICAL_CHARACTER_RECOGNITION(new Range(0x2440, 0x245F)),
		ENCLOSED_ALPHANUMERICS(new Range(0x2460, 0x24FF)),
		BOX_DRAWING(new Range(0x2500, 0x257F)),
		BLOCK_ELEMENTS(new Range(0x2580, 0x259F)),
		GEOMETRIC_SHAPES(new Range(0x25A0, 0x25FF)),
		MISCELLANEOUS_SYMBOLS(new Range(0x2600, 0x26FF)),
		DINGBATS(new Range(0x2700, 0x27BF)),
		MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A(new Range(0x27C0, 0x27EF)),
		SUPPLEMENTAL_ARROWS_A(new Range(0x27F0, 0x27FF)),
		BRAILLE_PATTERNS(new Range(0x2800, 0x28FF)),
		SUPPLEMENTAL_ARROWS_B(new Range(0x2900, 0x297F)),
		MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B(new Range(0x2980, 0x29FF)),
		SUPPLEMENTAL_MATHEMATICAL_OPERATORS(new Range(0x2A00, 0x2AFF)),
		MISCELLANEOUS_SYMBOLS_AND_ARROWS(new Range(0x2B00, 0x2BFF)),
		GLAGOLITIC(new Range(0x2C00, 0x2C5F)),
		LATIN_EXTENDED_C(new Range(0x2C60, 0x2C7F)),
		COPTIC(new Range(0x2C80, 0x2CFF)),
		GEORGIAN_SUPPLEMENT(new Range(0x2D00, 0x2D2F)),
		TIFINAGH(new Range(0x2D30, 0x2D7F)),
		ETHIOPIC_EXTENDED(new Range(0x2D80, 0x2DDF)),
		CYRILLIC_EXTENDED_A(new Range(0x2DE0, 0x2DFF)),
		SUPPLEMENTAL_PUNCTUATION(new Range(0x2E00, 0x2E7F)),
		CJK_RADICALS_SUPPLEMENT(new Range(0x2E80, 0x2EFF)),
		KANGXI_RADICALS(new Range(0x2F00, 0x2FDF)),
		IDEOGRAPHIC_DESCRIPTION_CHARACTERS(new Range(0x2FF0, 0x2FFF)),
		CJK_SYMBOLS_AND_PUNCTUATION(new Range(0x3000, 0x303F)),
		HIRAGANA(new Range(0x3040, 0x309F)),
		KATAKANA(new Range(0x30A0, 0x30FF)),
		BOPOMOFO(new Range(0x3100, 0x312F)),
		HANGUL_COMPATIBILITY_JAMO(new Range(0x3130, 0x318F)),
		KANBUN(new Range(0x3190, 0x319F)),
		BOPOMOFO_EXTENDED(new Range(0x31A0, 0x31BF)),
		CJK_STROKES(new Range(0x31C0, 0x31EF)),
		KATAKANA_PHONETIC_EXTENSIONS(new Range(0x31F0, 0x31FF)),
		ENCLOSED_CJK_LETTERS_AND_MONTHS(new Range(0x3200, 0x32FF)),
		CJK_COMPATIBILITY(new Range(0x3300, 0x33FF)),
		CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A(new Range(0x3400, 0x4DBF)),
		YIJING_HEXAGRAM_SYMBOLS(new Range(0x4DC0, 0x4DFF)),
		CJK_UNIFIED_IDEOGRAPHS(new Range(0x4E00, 0x9FFF)),
		YI_SYLLABLES(new Range(0xA000, 0xA48F)),
		YI_RADICALS(new Range(0xA490, 0xA4CF)),
		LISU(new Range(0xA4D0, 0xA4FF)),
		VAI(new Range(0xA500, 0xA63F)),
		CYRILLIC_EXTENDED_B(new Range(0xA640, 0xA69F)),
		BAMUM(new Range(0xA6A0, 0xA6FF)),
		MODIFIER_TONE_LETTERS(new Range(0xA700, 0xA71F)),
		LATIN_EXTENDED_D(new Range(0xA720, 0xA7FF)),
		SYLOTI_NAGRI(new Range(0xA800, 0xA82F)),
		COMMON_INDIC_NUMBER_FORMS(new Range(0xA830, 0xA83F)),
		PHAGS_PA(new Range(0xA840, 0xA87F)),
		SAURASHTRA(new Range(0xA880, 0xA8DF)),
		DEVANAGARI_EXTENDED(new Range(0xA8E0, 0xA8FF)),
		KAYAH_LI(new Range(0xA900, 0xA92F)),
		REJANG(new Range(0xA930, 0xA95F)),
		HANGUL_JAMO_EXTENDED_A(new Range(0xA960, 0xA97F)),
		JAVANESE(new Range(0xA980, 0xA9DF)),
		CHAM(new Range(0xAA00, 0xAA5F)),
		MYANMAR_EXTENDED_A(new Range(0xAA60, 0xAA7F)),
		TAI_VIET(new Range(0xAA80, 0xAADF)),
		MEETEI_MAYEK(new Range(0xABC0, 0xABFF)),
		HANGUL_SYLLABLES(new Range(0xAC00, 0xD7AF)),
		HANGUL_JAMO_EXTENDED_B(new Range(0xD7B0, 0xD7FF)),
		HIGH_SURROGATES(new Range(0xD800, 0xDB7F)),
		HIGH_PRIVATE_USE_SURROGATES(new Range(0xDB80, 0xDBFF)),
		LOW_SURROGATES(new Range(0xDC00, 0xDFFF)),
		PRIVATE_USE_AREA(new Range(0xE000, 0xF8FF)),
		CJK_COMPATIBILITY_IDEOGRAPHS(new Range(0xF900, 0xFAFF)),
		ALPHABETIC_PRESENTATION_FORMS(new Range(0xFB00, 0xFB4F)),
		ARABIC_PRESENTATION_FORMS_A(new Range(0xFB50, 0xFDFF)),
		VARIATION_SELECTORS(new Range(0xFE00, 0xFE0F)),
		VERTICAL_FORMS(new Range(0xFE10, 0xFE1F)),
		COMBINING_HALF_MARKS(new Range(0xFE20, 0xFE2F)),
		CJK_COMPATIBILITY_FORMS(new Range(0xFE30, 0xFE4F)),
		SMALL_FORM_VARIANTS(new Range(0xFE50, 0xFE6F)),
		ARABIC_PRESENTATION_FORMS_B(new Range(0xFE70, 0xFEFF)),
		HALFWIDTH_AND_FULLWIDTH_FORMS(new Range(0xFF00, 0xFFEF)),
		SPECIALS(new Range(0xFFF0, 0xFFFF)),
		LINEAR_B_SYLLABARY(new Range(0x10000, 0x1007F)),
		LINEAR_B_IDEOGRAMS(new Range(0x10080, 0x100FF)),
		AEGEAN_NUMBERS(new Range(0x10100, 0x1013F)),
		ANCIENT_GREEK_NUMBERS(new Range(0x10140, 0x1018F)),
		ANCIENT_SYMBOLS(new Range(0x10190, 0x101CF)),
		PHAISTOS_DISC(new Range(0x101D0, 0x101FF)),
		LYCIAN(new Range(0x10280, 0x1029F)),
		CARIAN(new Range(0x102A0, 0x102DF)),
		OLD_ITALIC(new Range(0x10300, 0x1032F)),
		GOTHIC(new Range(0x10330, 0x1034F)),
		UGARITIC(new Range(0x10380, 0x1039F)),
		OLD_PERSIAN(new Range(0x103A0, 0x103DF)),
		DESERET(new Range(0x10400, 0x1044F)),
		SHAVIAN(new Range(0x10450, 0x1047F)),
		OSMANYA(new Range(0x10480, 0x104AF)),
		CYPRIOT_SYLLABARY(new Range(0x10800, 0x1083F)),
		IMPERIAL_ARAMAIC(new Range(0x10840, 0x1085F)),
		PHOENICIAN(new Range(0x10900, 0x1091F)),
		LYDIAN(new Range(0x10920, 0x1093F)),
		KHAROSHTHI(new Range(0x10A00, 0x10A5F)),
		OLD_SOUTH_ARABIAN(new Range(0x10A60, 0x10A7F)),
		AVESTAN(new Range(0x10B00, 0x10B3F)),
		INSCRIPTIONAL_PARTHIAN(new Range(0x10B40, 0x10B5F)),
		INSCRIPTIONAL_PAHLAVI(new Range(0x10B60, 0x10B7F)),
		OLD_TURKIC(new Range(0x10C00, 0x10C4F)),
		RUMI_NUMERAL_SYMBOLS(new Range(0x10E60, 0x10E7F)),
		KAITHI(new Range(0x11080, 0x110CF)),
		CUNEIFORM(new Range(0x12000, 0x123FF)),
		CUNEIFORM_NUMBERS_AND_PUNCTUATION(new Range(0x12400, 0x1247F)),
		EGYPTIAN_HIEROGLYPHS(new Range(0x13000, 0x1342F)),
		BYZANTINE_MUSICAL_SYMBOLS(new Range(0x1D000, 0x1D0FF)),
		MUSICAL_SYMBOLS(new Range(0x1D100, 0x1D1FF)),
		ANCIENT_GREEK_MUSICAL_NOTATION(new Range(0x1D200, 0x1D24F)),
		TAI_XUAN_JING_SYMBOLS(new Range(0x1D300, 0x1D35F)),
		COUNTING_ROD_NUMERALS(new Range(0x1D360, 0x1D37F)),
		MATHEMATICAL_ALPHANUMERIC_SYMBOLS(new Range(0x1D400, 0x1D7FF)),
		MAHJONG_TILES(new Range(0x1F000, 0x1F02F)),
		DOMINO_TILES(new Range(0x1F030, 0x1F09F)),
		ENCLOSED_ALPHANUMERIC_SUPPLEMENT(new Range(0x1F100, 0x1F1FF)),
		ENCLOSED_IDEOGRAPHIC_SUPPLEMENT(new Range(0x1F200, 0x1F2FF)),
		CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B(new Range(0x20000, 0x2A6DF)),
		CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C(new Range(0x2A700, 0x2B73F)),
		CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT(new Range(0x2F800, 0x2FA1F)),
		TAGS(new Range(0xE0000, 0xE007F)),
		VARIATION_SELECTORS_SUPPLEMENT(new Range(0xE0100, 0xE01EF)),
		SUPPLEMENTARY_PRIVATE_USE_AREA_A(new Range(0xF0000, 0xFFFFF)),
		SUPPLEMENTARY_PRIVATE_USE_AREA_B(new Range(0x100000, 0x10FFFF)),
		
		_UNUSED(
				new Range(0x0840, 0x8FF),    // between SAMARITAN and DEVANAGARI
				new Range(0x1AB0, 0x1AFF),   // between TAI_TAM and BALINESE)
				new Range(0x1BC0, 0x1BFF),   // between SUDANESE and LEPCHA
				new Range(0x1C80, 0x1CCF),   // between OL_CHIKI and VEDIC_EXTENSIONS
				new Range(0x2FE0, 0x2FEF),   // between KANGXI_RADICALS and IDEOGRAPHIC_DESCRIPTION_CHARACTERS
				new Range(0xA9E0, 0xA9FF),   // between JAVANESE and CHAM
				new Range(0xAAE0, 0xABBF),   // between TAI_VIET and MEETEI_MAYEK
				new Range(0x10200, 0x1027F), // between PHAISTOS_DISC and LYCIAN
				new Range(0x102E0, 0x102FF), // between CARIAN and OLD_ITALIC
				new Range(0x10350, 0x1037F), // between GOTHIC and UGARITIC
				new Range(0x103E0, 0x103FF), // between OLD_PERSIAN and DESERET
				new Range(0x104B0, 0x107FF), // between OSMANYA and CYPRIOT_SYLLABARY
				new Range(0x10860, 0x108FF), // between IMPERIAL_ARAMAIC and PHOENICIAN
				new Range(0x10940, 0x109FF), // between LYDIAN and KHAROSHTHI
				new Range(0x10A80, 0x10AFF), // between OLD_SOUTH_ARABIAN and AVESTAN
				new Range(0x10B80, 0x10BFF), // between INSCRIPTIONAL_PAHLAVI and OLD_TURKIC
				new Range(0x10C50, 0x10E5F), // between OLD_TURKIC and RUMI_NUMERAL_SYMBOLS
				new Range(0x10E80, 0x1107F), // between RUMI_NUMERAL_SYMBOLS and KAITHI
				new Range(0x110D0, 0x11FFF), // between KAITHI and CUNEIFORM
				new Range(0x12480, 0x12FFF), // between CUNEIFORM_NUMBERS_AND_PUNCTUATION and EGYPTIAN_HIEROGLYPHS
				new Range(0x13430, 0x1CFFF), // between EGYPTIAN_HIEROGLYPHS and BYZANTINE_MUSICAL_SYMBOLS
				new Range(0x1D250, 0x1D2FF), // between ANCIENT_GREEK_MUSICAL_NOTATION and TAI_XUAN_JING_SYMBOLS
				new Range(0x1D380, 0x1D3FF), // between COUNTING_ROD_NUMERALS and MATHEMATICAL_ALPHANUMERIC_SYMBOLS
				new Range(0x1D800, 0x1EFFF), // between MATHEMATICAL_ALPHANUMERIC_SYMBOLS and MAHJONG_TILES
				new Range(0x1F0a0, 0x1F0FF), // between DOMINO_TILES and ENCLOSED_ALPHANUMERIC_SUPPLEMENT
				new Range(0x1F300, 0x1FFFF), // between ENCLOSED_IDEOGRAPHIC_SUPPLEMENT and CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				new Range(0x2a6e0, 0x2a6FF), // between CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B and CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
				new Range(0x2B740, 0x2F7FF), // between CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C and CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
				new Range(0xE0080, 0xE00FF), // between TAGS and VARIATION_SELECTORS_SUPPLEMENT
				new Range(0x2FA20, 0xDFFFF), // between CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT and TAGS
				new Range(0xE01F0, 0xEFFFF));// between VARIATION_SELECTORS_SUPPLEMENT and SUPPLEMENTARY_PRIVATE_USE_AREA_A
		
		private Set<Range> ranges;
		
		private CharacterBlock(Range... ranges) {
			this.ranges = new HashSet<Range>();
			for(Range range : ranges) {
				this.ranges.add(range);
			}
			
		}
		
		public boolean contains(int codePoint) {
			for(Range range : this.ranges) {
				if(codePoint >= range.getLowerBound() && codePoint <= range.getUpperBound()) {
					return true;
				}
			}
			
			return false;
		}
		
		public Set<Range> getRanges() {
			return this.ranges;
		}
	}
}
