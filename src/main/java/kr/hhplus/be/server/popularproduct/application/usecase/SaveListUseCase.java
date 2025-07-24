package kr.hhplus.be.server.popularproduct.application.usecase;

/**
 * [UseCase - Port In]
 * 인기 상품 목록을 저장하는 유스케이스에 대한 추상 정의.
 *
 * 이 인터페이스는 애플리케이션 계층에서
 * 인기 상품 목록을 삭제/저장하는 기능을 외부(presentation, facade 등)에 노출하기 위한 계약(Contract)이다.
 *
 * 구현체는 service 패키지 내에서 정의되며,
 * presentation 계층은 이 인터페이스만 의존함으로써 구현체에 대한 결합을 피할 수 있다.
 */
public interface SaveListUseCase {
    /**
     * 인기 상품 목록을 저장한다.
     * 현재 미구현
     */
    void replaceAll();
}
