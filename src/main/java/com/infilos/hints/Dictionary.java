package com.infilos.hints;

import com.infilos.hints.eval.LcsEvaluator;
import com.infilos.hints.utils.Pair;
import com.infilos.hints.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Dictionary<T> {
    private static final Logger log = LoggerFactory.getLogger(Dictionary.class);

    private final Configure configure;
    private final List<Candidate<T>> candidates = new ArrayList<>();

    protected Dictionary(List<T> items, Configure configure) {
        this.configure = configure;
        for (T item : items) {
            Map<String, Attribute> attributes = new HashMap<>();

            for (String field : fields()) {
                String origin = extract(item, field);
                String pinyin = null;
                String capital = null;

                if (Strings.nonBlank(origin) && Strings.isChinese(origin)) {
                    if (configure.isPinyinEnabled(field)) {
                        pinyin = Strings.formtPinyin(origin);
                    }
                    if (configure.isCapitalEnabled(field)) {
                        capital = Strings.formatCapital(origin);
                    }
                }
                attributes.put(field, new Attribute(origin, pinyin, capital));
            }

            candidates.add(new Candidate<>(item, attributes));
        }
    }

    public List<T> allItems() {
        return candidates.stream().map(Candidate::value).collect(Collectors.toList());
    }

    /**
     * Support fields for hinting.
     */
    @Nonnull
    protected abstract List<String> fields();

    /**
     * Extract the supported field's value as string.
     */
    @Nullable
    protected abstract String extract(T item, String field);

    public List<T> hint(String field, String word) {
        return hint(field, word, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    public List<T> hint(String field, String word, int size) {
        if (!fields().contains(field) || Strings.isBlank(word) || size <= 0) {
            return Collections.emptyList();
        }

        List<T> hints = new ArrayList<>();

        if (Strings.isCapitals(word)) {
            hints = combine(
                findEquals(field, Attribute::capital, word),
                findMatches(field, Attribute::capital, word)
            );
        } else if (Strings.isPinyin(word)) {
            // if treat english word as pinyin, the result would be wrong(without fallback to origin)
            // so the pinyin check logic should be more strict, if nothing found, fallback to origin
            hints = combine(
                findEquals(field, Attribute::pinyin, word.toUpperCase()),
                findMatches(field, Attribute::pinyin, word.toUpperCase())
            );
        }

        if (hints.isEmpty()) {
            hints = combine(
                findEquals(field, Attribute::origin, word),
                findMatches(field, Attribute::origin, word)
            );
        }

        return slice(hints, size);
    }

    protected List<T> findEquals(String field, Function<Attribute, String> attributeExtractor, String word) {
        List<T> items = candidates.stream()
            .filter(cand -> {
                if (!cand.hasField(field)) {
                    return false;
                }
                String fieldAttriValue = attributeExtractor.apply(cand.fieldAttribute(field));
                if (Strings.isBlank(fieldAttriValue)) {
                    return false;
                }

                return word.equalsIgnoreCase(fieldAttriValue);
            })
            .map(Candidate::value)
            .collect(Collectors.toList());

        items.forEach(i -> log.debug("findEquals [{},{}]: {}", field, word, i));

        return items;
    }

    @SuppressWarnings("unchecked")
    protected List<T> findMatches(String field, Function<Attribute, String> attributeExtractor, String word) {
        Evaluator<Object> evaluator = (Evaluator<Object>) configure.evaluator().orElse(LcsEvaluator.Instance);
        List<T> items = candidates.stream()
            .map(cand -> {
                if (cand.hasField(field)) {
                    String fieldAttriValue = attributeExtractor.apply(cand.fieldAttribute(field));
                    if (Strings.isBlank(fieldAttriValue)) {
                        return Pair.of(cand, "");
                    } else {
                        return Pair.of(cand, fieldAttriValue);
                    }
                } else {
                    return Pair.of(cand, "");
                }
            })
            .filter(candAttriValue -> Strings.nonBlank(candAttriValue.right()))
            .map(candAttriValue -> Pair.of(
                candAttriValue.left(),
                evaluator.evaluate(candAttriValue.right(), word)
            ))
            .filter(candEvalValue -> evaluator.check(candEvalValue.right()))
            .sorted((c1, c2) -> evaluator.compare(c1.right(), c2.right()))
            .map(candEvalValue -> candEvalValue.left().value())
            .collect(Collectors.toList());

        items.forEach(i -> log.debug("findMatches [{},{}]: {}", field, word, i));

        return items;
    }

    /**
     * Combine result items with distinct.
     */
    @SuppressWarnings("unchecked")
    protected List<T> combine(List<T>... items) {
        List<T> result = new ArrayList<>();

        for (List<T> its : items) {
            for (T item : its) {
                if (!result.contains(item)) {
                    result.add(item);
                }
            }
        }

        return result;
    }

    protected List<T> slice(List<T> items, int size) {
        if (items.isEmpty()) {
            return items;
        }

        return items.subList(0, Math.min(size, items.size()));
    }

    public static final class StringDictionary extends Dictionary<String> {

        protected StringDictionary(List<String> items, Configure configure) {
            super(items, configure);
        }

        @Nonnull
        @Override
        protected List<String> fields() {
            return Collections.singletonList("string");
        }

        @Nullable
        @Override
        protected String extract(String item, String field) {
            return item;
        }

        public List<String> hint(String word) {
            return super.hint("string", word, Integer.MAX_VALUE);
        }

        public List<String> hint(String word, int size) {
            return super.hint("string", word, size);
        }
    }

    public static StringDictionary create(List<String> items, Configure configure) {
        return new StringDictionary(items, configure);
    }

    public static StringDictionary create(List<String> items) {
        return new StringDictionary(items, Configure.builder().build());
    }
}
