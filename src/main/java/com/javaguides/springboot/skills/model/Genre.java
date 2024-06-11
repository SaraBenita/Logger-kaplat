package com.javaguides.springboot.skills.model;


public enum Genre {
    SCI_FI, NOVEL, HISTORY, MANGA, ROMANCE, PROFESSIONAL;
    public static Genre getGenre(String genre)
    {
        for (Genre g : Genre.values()) {
            if (g.name().equals(genre)) {
                return g;
            }
        }
        return null;
    }
}
