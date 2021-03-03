package expiryDate;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 테스트 코드의 작성 순서
 *
 * 1. 쉬운 케이스에서부터 어려운 케이스 순서로 작성한다.
 * 복잡한 상황을 처음부터 구현하면 해당 테스트를 통과하기 위해서 한 번에 많은 로직을 구현해야 한다.
 * 주어진 모든 조건을 충족하는 경우, 또는 모두 충족하지 않는 극단적인 케이스부터 작성하는 것이 좋다.
 *
 * 2. 예외적인 케이스에서부터 일반적인 케이스 순서로 작성한다.
 * 예외 상황을 고려하지 않은 코드에 예외 상황을 반영하려면 코드의 구조를 뒤집거나
 * 코드 중간에 예외를 처리하기 위한 분기가 삽입된다.
 *
 * 매달 비용 지불을 하는 유료 서비스
 * - 서비스를 사용하려면 매달 1만원을 선불로 납부한다. 납부일 기준으로 한 달 뒤가 서비스 만료일이다.
 * - 2개월 이상 요금을 납부할 수 있다.
 * - 10만원을 납부하면 서비스를 1년 제공한다.
 */
public class ExpiryDateCalculatorTest {
    @Test
    void 만원_납부하면_한달_뒤가_만료일이_됨(){
        assertExpiryDate(
                PayData.builder()
                    .billingDate(LocalDate.of(2021, 3, 1))
                    .payAmount(10_000)
                    .build(),
                LocalDate.of(2021, 4, 1)
        );

        assertExpiryDate(
                PayData.builder()
                    .billingDate(LocalDate.of(2021, 5, 5))
                    .payAmount(10_000)
                    .build(),
                LocalDate.of(2021, 6, 5)
        );
    }

    @Test
    void 납부일과_한달_뒤_일자가_같지_않음(){
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2021, 1, 31))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2021, 2, 28)
        );

        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2021, 5, 31))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2021, 6, 30)
        );

        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2020, 1, 31))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2020, 2, 29)
        );
    }
    
    @Test
    void 첫_납부일과_만료일_일자가_다를때_만원_납부하면_첫_납부일_기준_갱신(){
        PayData payData = PayData.builder()
                .firstBillingDate(LocalDate.of(2021, 1, 31))
                .billingDate(LocalDate.of(2021, 2, 28))
                .payAmount(10_000)
                .build();

        assertExpiryDate(payData, LocalDate.of(2021, 3, 31));

        PayData payData2 = PayData.builder()
                .firstBillingDate(LocalDate.of(2021, 1, 30))
                .billingDate(LocalDate.of(2021, 2, 28))
                .payAmount(10_000)
                .build();

        assertExpiryDate(payData2, LocalDate.of(2021, 3, 30));

        PayData payData3 = PayData.builder()
                .firstBillingDate(LocalDate.of(2021, 5, 31))
                .billingDate(LocalDate.of(2021, 6, 30))
                .payAmount(10_000)
                .build();

        assertExpiryDate(payData3, LocalDate.of(2021, 7, 31));
    }

    @Test
    void 이만원_이상_납부하면_비례해서_만료일_계산(){
        assertExpiryDate(
                PayData.builder()
                    .billingDate(LocalDate.of(2021, 3, 1))
                    .payAmount(20_000)
                    .build(),
                LocalDate.of(2021, 5, 1)
        );

        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2021, 3, 1))
                        .payAmount(30_000)
                        .build(),
                LocalDate.of(2021, 6, 1)
        );
    }

    @Test
    void 첫_납부일과_만료일_일자가_다를때_이만원_이상_납부() {
        assertExpiryDate(
                PayData.builder()
                        .firstBillingDate(LocalDate.of(2021, 1, 31))
                        .billingDate(LocalDate.of(2021, 2, 28))
                        .payAmount(20_000)
                        .build(),
                LocalDate.of(2021, 4, 30)
        );

        assertExpiryDate(
                PayData.builder()
                        .firstBillingDate(LocalDate.of(2021, 3, 31))
                        .billingDate(LocalDate.of(2021, 4, 30))
                        .payAmount(30_000)
                        .build(),
                LocalDate.of(2021, 7, 31)
        );
    }

    @Test
    void 십만원을_납부하면_1년_제공() {
        assertExpiryDate(
                PayData.builder()
                        .billingDate(LocalDate.of(2021, 1, 28))
                        .payAmount(100_000)
                        .build(),
                LocalDate.of(2022, 1, 28)
        );
    }

    private void assertExpiryDate(PayData payData, LocalDate expectedExpiryDate){
        ExpiryDateCalculator cal = new ExpiryDateCalculator();
        LocalDate realExpiryDate = cal.calculateExpiryDate(payData);
        assertEquals(expectedExpiryDate, realExpiryDate);
    }
}
