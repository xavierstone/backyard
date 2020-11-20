package com.xavierstone.backyard;

// User added review of a campsite
public class Rant {
    // Attributes
    private byte stars; // Star rating, 1-5
    private String words; // review content

    // References
    private final User author; // reviewer
    private final Site site; // campsite the review is of

    public Rant(User author, Site site, byte stars, String words) {
        // Copy arguments
        this.author = author;
        this.site = site;
        this.stars = stars;
        this.words = words;
    }

    // Getters
    public User getAuthor() { return author; }
    public Site getSite() { return site; }
    public byte getStars() { return stars; }
    public String getWords() { return words; }

    // Permission check
    public boolean hasPermission(User author) { return (this.author == author); }

    // Setters (with permissions)
    public void setStars(User author, byte stars) { if (hasPermission(author)) this.stars = stars; }

    public void setWords(User author, String words) { if (hasPermission(author)) this.words = words; }

    // Delete site
    // TODO: implement delete site
}
