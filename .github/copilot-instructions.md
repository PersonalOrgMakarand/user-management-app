When writing Java methods, always:
- Add descriptive JavaDoc comments
- Include input validation using Objects.requireNonNull or explicit checks
- Use early returns for invalid or edge-case conditions
- Use meaningful and self-explanatory variable names
- Keep methods focused on a single responsibility
- Include at least one example usage in JavaDoc comments
- Throw appropriate exceptions with meaningful messages
- Prefer immutable variables using `final` where possible

## Example Method Pattern
```java
/**
 * Calculates the discount amount for a given purchase value.
 *
 * <p>Example usage:
 * <pre>
 * double discount = calculateDiscount(5000, 10);
 * System.out.println(discount); // 500.0
 * </pre>
 *
 * @param purchaseAmount total purchase amount
 * @param discountPercentage percentage discount to apply
 * @return calculated discount amount
 * @throws IllegalArgumentException if values are invalid
 */
public double calculateDiscount(double purchaseAmount, double discountPercentage) {

    // Input validation
    if (purchaseAmount <= 0) {
        throw new IllegalArgumentException("Purchase amount must be greater than zero");
    }

    if (discountPercentage < 0 || discountPercentage > 100) {
        throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
    }

    // Early return
    if (discountPercentage == 0) {
        return 0;
    }

    // Business logic
    double calculatedDiscount = purchaseAmount * (discountPercentage / 100);

    return calculatedDiscount;
}