package com.example.pawel.errorcontrol;

import java.util.ArrayList;

public class Hamming extends CodeBase
{
	private int enc[];

	@Override
	int[] encode()
	{
        int n = data.length;                                    // dlugosc slowa danych
        int wordSize = calcWordSize(n);                         // dlugosc slowa kodowego
        int redundantBits = wordSize - n;                      	// ilosc nadmiarowych bitow
        ArrayList<String> positionsOf1 = new ArrayList<>();    	// pozycje bitow zawierajacych 1 (w zapisie binarnym)
        code = new int[wordSize];								// tablica zawierajaca zakodowane slowo

        // uzupelnij bity danych d
        for(int i = 0, j = 0; i < wordSize; i++)
        {
            if(!isPowerOf2(i+1))
            {
            	code[i] = data[n - j - 1];
                j++;

                // zapisz pozycje bitow na ktorych wystepuje 1 (uzywane do obliczania wartosci bitow c)
                if(code[i] == 1)
                {
                    String binaryPosition = padWithZeros(Integer.toBinaryString(i+1), redundantBits);  // pozycja danej jedynki w zapisie binarnym
                    positionsOf1.add(binaryPosition);
                }
            }
        }

        // tablica zawierajca wartosc kolejnych bitow c
        int[] cBits = new int[redundantBits];
		for(int i = 0, count = 0; i < redundantBits; i++)
        {
            // zlicz ilosc '1' w kolumnach pozycji przedstawionych w zapisie binarmy (ile razy wystepuje '1' w kazdej kolumnie)
            for(int j = 0; j < positionsOf1.size(); j++)
            {
                if(positionsOf1.get(j).charAt(redundantBits - i - 1) == '1')
                    count++;
            }

            if(count % 2 == 0)
				cBits[i] = 0;
            else
				cBits[i] = 1;

			count = 0;
        }

        // uzupelnij bity danych c
		for(int i = 0, j = 0; i < wordSize; i++)
		{
            if(isPowerOf2(i+1))
            {
                code[i] = cBits[j];
                j++;
            }
        }

        // odwroc tablice i zapisz ja w innej zmiennej sluzacej do dekodowania (wartosc code moze zostac zmieniona przed dekodowaniem)
        code = reverseArray(code);
		enc = code;

		return code;
	}

	@Override
	int[] decode()
	{
		int[] encoded = reverseArray(enc);						// zakodowane slowo danych
		int n = encoded.length;                                 // dlugosc zakodowanego slowa danych
		int d = 0;												// ilosc bitow danych

		// oblicz ilosc bitow d
		for(int i = 0; i < n ; i++)
		{
			if(!isPowerOf2(i+1))
				d++;
		}
		data = new int[d];										// slowo zdekodowane

		// odekoduj slowo
		for(int i = 0, j = 0; i < n ; i++)
		{
			if(!isPowerOf2(n - i))
			{
				data[j] = encoded[n - i - 1];
				j++;
			}
		}

		return data;
	}

	@Override
	void fix()
	{
		int n=code.length;
		int d=0;
		int nadmiar=0;
		for (int i=0; i<n; i++)		// liczenie długości danych
		{
			if (Math.pow(2, nadmiar)-1!=i) d++;
			else nadmiar++;
		}
		
		data = new int[d];
		
		int maska=0;
		d=0;
		nadmiar=0;
		
		for (int i=0; i<n; i++)
		{
			// kontrola poprawności
			if (code[i]==1) maska^=i+1;
			
			// określanie typu bitów
			if (Math.pow(2, nadmiar)-1!=i)		// bit danych
			{
				d++;
				type[i]=0;			// poprawny (jak na razie) bit danych
			}
			else
			{
				type[i]=3;				// poprawny (jak na razie) bit redundantny
				nadmiar++;
			}
		}
		
		if (maska!=0)					// wykryto błąd
		{
			errors++;
			int numer=maska-1;			// numeracja bitów od 1, tablicy - od 0

			if (numer<code.length)
			{
				if (type[numer]==0) type[numer]=1;	// korygujemy bit danych
				else if (type[numer]==3) type[numer]=4;	// korygujemy bit redundantny

				if (code[numer]==1) code[numer]=0;
				else code[numer]=1;
			}
		}
	}

	// sprawdza czy liczba jest podzielna przez 2
	private boolean isPowerOf2(int n)
	{
		return (n & -n) == n;
	}

	// dodaje 0 na poczatku stringa (np. dane wejsciowe: string = "11", size = 5; dane wyjsciowe: string = "00011")
	private String padWithZeros(String string, int size)
    {
        StringBuffer stringBuffer = new StringBuffer(string);
        while(stringBuffer.length() < size)
        {
            stringBuffer.insert(0, '0');
        }

        return stringBuffer.toString();
    }

	// oblicza dlugosc zakodowanego slowa
	private int calcWordSize(int bits)
	{
		int wordSize = 0;
		int bitsLeft = bits;

		// oblicz ilosc bitow nadmiarowych (bity na pozycji bedacej potega 2)
		int i = 1;
		while(bitsLeft > 0)
		{
			if(isPowerOf2(i))
				wordSize++;
			else
				bitsLeft--;

			i++;
		}

		// dlugosc slowa kodowego = ilosc bitow nadmiarowych + ilosc bitow danych
		wordSize += bits;

		return wordSize;
	}

    // odwraca tablice intow
	private int[] reverseArray(int[] array)
	{
		int[] reversedArray = new int[array.length];

		for(int i=0; i < array.length / 2; i++)
		{
			int t = array[i];
			reversedArray[i] = array[array.length - i - 1];
			reversedArray[array.length - i - 1 ] = t;
		}

		return reversedArray;
	}
}
