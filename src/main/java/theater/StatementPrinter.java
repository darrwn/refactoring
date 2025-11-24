package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 *
 * @null not allowed
 */
public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Map<String, Play> getPlays() {
        return plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     * @null not allowed
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;

        final StringBuilder result =
                new StringBuilder("Statement for "
                        + invoice.getCustomer()
                        + System.lineSeparator());

        final NumberFormat frmt =
                NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance performance : invoice.getPerformances()) {

            volumeCredits += result1(performance);

            result.append(
                    String.format(
                            "  %s: %s (%s seats)%n",
                            getPlay(performance).getName(),
                            frmt.format(
                                    (double) getAmount(performance)
                                            / Constants.PERCENT_FACTOR),
                            performance.getAudience()));

            totalAmount += getAmount(performance);
        }

        result.append(
                String.format(
                        "Amount owed is %s%n",
                        frmt.format(
                                (double) totalAmount
                                        / Constants.PERCENT_FACTOR)));
        result.append(
                String.format(
                        "You earned %s credits%n", volumeCredits));

        return result.toString();
    }

    private int result1(Performance performance) {
        int result1 = 0;
        result1 += Math.max(
                performance.getAudience()
                        - Constants.BASE_VOLUME_CREDIT_THRESHOLD,
                0);

        if ("comedy".equals(getPlay(performance).getType())) {
            result1 += performance.getAudience()
                    / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result1;
    }

    private Play getPlay(Performance performance) {
        return plays.get(performance.getPlayId());
    }

    private int getAmount(Performance performance) {
        int thisAmount = 0;

        switch (getPlay(performance).getType()
        ) {
            case "tragedy":
                thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience()
                        > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case "comedy":
                thisAmount = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience()
                        > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE
                        * performance.getAudience();
                break;

            default:
                throw new RuntimeException(
                        String.format("unknown type: %s",
                                getPlay(performance).getType()
                        ));
        }
        return thisAmount;
    }
}
