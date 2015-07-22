package com.krypto.movietalk;

/**
 * Stores and returns critic and his review info for a particular movie
 */
public class Review {

    private String mCritic, mQuote;

    public Review(String critic, String quote) {

        mCritic = critic;
        mQuote = quote;

    }

    public String getCritic() {
        return mCritic;
    }

    public String getQuote() {
        return mQuote;
    }


}
