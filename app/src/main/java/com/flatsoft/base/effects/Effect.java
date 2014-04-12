package com.flatsoft.base.effects;

/**
 * Created by adelnizamutdinov on 09/04/2014
 */
public interface Effect<T> {
    public static final Bold          BOLD           = new Bold();
    public static final Italic        ITALIC         = new Italic();
    public static final Underline     UNDERLINE      = new Underline();
    public static final StrikeThrough STRIKE_THROUGH = new StrikeThrough();
    public static final OrderedList   ORDERED_LIST   = new OrderedList(10);
    public static final UnorderedList UNORDERED_LIST = new UnorderedList(10);
    public static final Effect[]      EFFECTS        = {BOLD, ITALIC, UNDERLINE, STRIKE_THROUGH, ORDERED_LIST, UNORDERED_LIST};

    Class<T> clazz();

    T newInstance();

    boolean appliesTo(T instance);
}
