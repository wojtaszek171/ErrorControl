package com.example.pawel.errorcontrol;

/**
 * Created by pawel on 07.04.2018.
 */

public class Parity extends CodeBase
{
    @Override
    int[] encode()
    {
        int n = data.length;        // dlugosc slowa danych
        int parityBits = n/8;       // liczba bitow parzystosci
        n += parityBits;            // dlugosc zakodowanej wiadomosci = dlugosc wiadomosci + ilosc bitow parzystosci
        code = new int[n];          // tablica zawierajaca zakodowane slowo
        type = new int[n];

        // dodaj bit parzystosci w kazdym bajcie
        for (int i = 0; i < parityBits; i++)
        {
            int jedynki = 0;                                // ilosc jedynek w bajcie

            // zliczaj jedynki
            for (int j = 0; j < 8; j++)
            {
                code[i*9+j+1] = data[i*8+j];		    // zapisuj kolejne bity danego bajtu do slowa zakodowanego
                jedynki += data[i*8+j];			        // zliczaj jedynki
            }

            // zapisz bit parzystości
            if (jedynki % 2 == 1)
                code[i*9]=1;
            else
                code[i*9]=0;
        }

        return code;
    }

    @Override
    int[] decode()
    {
        int n = code.length;                    // dlugosc zakodowanych danych
        int bajty = n/9;                        // ilosc bajtow
        errors = 0;                             // ilsc bledow
        data = new int[bajty*8];                // odszyfrowane dane

        // dekoduj
        for (int i = 0; i < bajty; i++)
        {
            int jedynki = 0;                  // ilosc jedynek w bajcie

            for (int j = 0; j < 8; j++)
            {
                data[i*8+j] = code[i*9+j+1];		// przepisz dane
                jedynki += code[i*9+j+1];			// zliczaj jedynki
            }

            jedynki += code[i*9];				// dodaj bit parzystości

            // sprawdz poprawnosc transmisji
            if (jedynki % 2 == 0)
            {
                type[i*9]=3;				                // poprawny bit kontrolny

                for (int j = 1; j < 9; j++)                     // poprawne bity danych
                    type[i*9+j]=0;
            }
            else
            {
                errors++;                   // inkrementuj liczbe bledow
                type[i*9]=5;				// niepewny bit kontrolny

                for (int j = 1; j < 9; j++)     // niepewne bity danych
                    type[i*9+j]=2;
            }
        }

        return data;
    }

    @Override
    void fix()
    {
        int n = code.length;                        // dlugosc zakodowanych danych
        int bajty = n/9;                            // ilosc bajtow
        errors = 0;                                 // ilosc bledow
        type = new int[n];

        // napraw slowo danych
        for (int i = 0; i < bajty; i++)
        {
            int jedynki = 0;                          // ilosc jedynek w bajcie

            // zliczaj jedynki
            for (int j = 0; j < 8; j++)
                jedynki += code[i*9+j+1];

            jedynki+=code[i*9];				        // dolicz bit parzystości

            // sprawdz poprawnosc transmisji
            if (jedynki % 2 == 0)
            {
                type[i*9]=3;				            // poprawny bit kontrolny

                for (int j=1; j<9; j++)                 // poprawne bity danych
                    type[i*9+j]=0;
            }
            else
            {
                errors++;                               // inkrementuj liczbe bledow
                type[i*9]=5;				            // niepewny bit kontrolny

                for (int j = 1; j < 9; j++)                 // niepewne bity danych
                    type[i*9+j]=2;
            }
        }
    }
}
