# SpringBootの実装をテストするときのまとめ
## この資料について
* Spring bootを使ったプロジェクトをやっていて、その際にテストどう書くねんってなったからまとめた
* 基本調べて書いたのでもっとこうしたほうがきれいにかけるやで、っていうのがあればぜひ

## 実行環境
* Java 1.8
* SpringBoot 1.5.7.RELEASE
* 詳しくは下記のリポジトリ参照
## ここでやったことのコード
[spring-boot-test-sample](https://github.com/mitsuya-saida/spring-boot-test-sample)
## Serviceのテスト
#### とりあえず動かす
とりあえずJUnitで普通のテストを書いてみる  
まずはテスト対象のクラスを定義する
```java
// テスト対象のクラス
@Service
public class DemoService {

    public String greeting(String greet) {
        if (StringUtils.isEmpty(greet)) {
            return "Say something...";
        }

        return "hello";
    }
}

```
次にテストコードを書いてみる
```java
public class DemoServiceTest {

    private DemoService demoService;

    @Before
    public void setUp() {
        demoService = new DemoService();
    }

    @Test
    public void greeting_あいさつがないとき() {
        assertThat(demoService.greeting(null), is("Say something..."));
    }

    @Test
    public void greeting_あいさつがあるとき() {
        assertThat(demoService.greeting("something"), is("hello"));
    }
}


```
#### 解説
特に書くことないけどJUnitとhamcrestをつかってテスト書いた

## Mock化してテスト
何かしらのクラスをインスタンス化してテストするケースにて、呼ばれた経路だけ担保すればいいようなケースをテストしてみる  
ここではcontrollerのテストでやってみる
#### とりあえず動かす
ちょっと冗長な書き方してるけど気にしないでください。
```java
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/demo")
@Validated
public class DemoController {

    @Autowired
    private DemoService demoService;

    @Autowired
    private DemoRepository demoRepository;

    @GetMapping
    public String demo(@Valid DemoRequest demoRequest) {
        DemoEntity demoEntity = demoRepository.findByCode(demoRequest.getCode());
        String value = demoEntity.getValue();
        String ret = demoService.greeting(value);
        return ret;
    }
}

```
テストを書く
```java

public class DemoControllerTest {

    private static String GREET = "hoge";

    @Mock
    private DemoRepository demoRepository;

    @Mock
    private DemoService demoService;

    @InjectMocks
    private DemoController demoController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void 順番の担保() {
        DemoEntity demoEntity = DemoEntity.builder()
                .code("hoge")
                .value("fuga")
                .updateAt(null)
                .build();
        when(demoRepository.findByCode(anyObject())).thenReturn(demoEntity);
        when(demoService.greeting(anyObject())).thenReturn("hello");
        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setCode(GREET);
        String expected = demoController.demo(demoRequest);

        verify(demoRepository, times(1)).findByCode(GREET);
        verify(demoService, times(1)).greeting("fuga");

        assertThat("hello", is(expected));
    }

}

```
#### 解説
* リクエストされたパラメータでDemoRepositoryからデータ引いてその結果からDemoServiceの関数呼んでデータ返却、みたいな関数のテストを書いた。  
* Mock化するインスタンスには@Mockとつけて、Mockされたインスタンスをつっこむインスタンスには@InjectMocksをつけて宣言する
* @Beforeの部分で、それぞれ宣言したインスタンスを初期化して実際にMock化、つっこむ、ってところをやってる
* テストの関数の部分で、こう呼ばれたらこう返す、ってのをwhenとthenReturnで行っている。どの引数で呼ばれるかはあとで行っているのでanyObject()にしてる 
* 何回呼ばれるか、どの引数でよばれるかの確認はverifyで行っている
* 最終的になんの結果が呼ばれるかはassertThatとisで行っている
## Repositoryのテスト(CSVでテストデータ管理する)
テストデータをCSVで用意し、テスト前にそのデータでテーブルを初期化してRepositoryのテストを行う
#### とりあえず動かす
###### テスト対象のクラスをつくる
今回findByCodeのテストをしたいのでそれだけ宣言する
```java
package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface DemoRepository extends JpaRepository<DemoEntity, String>{
    public DemoEntity findByCode(String code);
}

```
###### テスト対象RepositoryのEntityをつくる
```java
@Table(name = "demo")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DemoEntity {

    @Id
    private String code;
    private String value;
    private ZonedDateTime updateAt;
}

```
###### テストコードを書く
CSVで読み込むために色々やっているのでコメントで軽く解説を残す
```java
package com.example.demo;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DbUnitConfiguration(
        dataSetLoader = ReplacementCsvDataSetLoader.class // ここでCSVでデータ読み込むReplacementDataSetLoaderのクラスを指定
)
@Transactional
public class DemoRepositoryTest {

    private static final String DATA_FILE_PATH = "/DemoRepository/";

    @Autowired
    private DemoRepository demoRepository;

    @DatabaseSetup(value = DATA_FILE_PATH + "findByCode/")
    @Test
    public void findByCodeTest() {
        DemoEntity expected = DemoEntity.builder()
                .code("hoge")
                .value("fuga")
                .updateAt(ZonedDateTime.parse("2017-01-01T00:00:00+09:00:00[Asia/Tokyo]"))
                .build();
        DemoEntity actual = demoRepository.findByCode("hoge");
        assertThat(actual, samePropertyValuesAs(expected));
    }
}

```
###### ReplacementDataSetLoaderを拡張する
```java
package com.example.demo;

import com.github.springtestdbunit.dataset.ReplacementDataSetLoader;

public class ReplacementCsvDataSetLoader extends ReplacementDataSetLoader {
    public ReplacementCsvDataSetLoader() {
        super(new CsvDataSetLoader()); // ここで実装したCsvをloadするLoaderを読み込む
    }
}

```
###### Csv用のLoaderを実装する
```java
package com.example.demo;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvURLDataSet;
import org.springframework.core.io.Resource;

public class CsvDataSetLoader extends AbstractDataSetLoader {
    public CsvDataSetLoader() {
    }

    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        return new CsvURLDataSet(resource.getURL());
    }
}

```
###### テストデータ
demoというテーブル名なのでresourcesにdemo.csvファイルを作成
```
code,value,update_at
hoge,fuga,2017-01-01 00:00:00
fuga,hoge,2017-01-01 00:00:00
```
###### table-ordering.txt
使用するテーブルを定義するファイル
```
demo
```
#### 解説
それぞれ解説する
###### 実装とEntity
* 特筆すべき事はとくにない。普通にRepositoryとEntityつくっただけ
* 強いて言うならZonedDataTimeはhibernateのバージョンが5.2以上じゃないと動かないので依存を少しいじらないといけない
  *spring-boot-starter-data-jpaの現行のバージョン(1.5.3くらい)だとhibernateのバージョンは5.0系

###### テストデータをCSVで読み込むところ
* けっこういろんなファイルをつくったが、自分でごりごり書くような内容はなく、用意されたものを使っただけ
* ReplacementDataSetLoaderのコンストラクタにてAbstractDataSetLoaderを拡張したLoaderを宣言すれば良い
* AbstractDataSetLoaderを拡張したCsvDataSetLoaderを自作したが、そこではcreateDataSet関数をOverrideした関数をつくればよく、そこの返却値ではDBUnitで用意されたCSV用のインスタンスを返すだけで良い
* CSVの読み込み方はディレクトリを指定して、そのディレクトリにあるtable-ordering.txtを読み込み、そこに定義されている同じ階層にあるテーブル名のファイルを読み込んでくれる
###### テストコード
* @DatabaseSetupで指定したディレクトリのデータをテーブルにぶっこんでからテストが動く
* その後は普通にデータを引いてきて、assertしているだけ
## リクエストパラメータのテスト
リクエストパラメータをDTOでやってる場合のテストを書いた  
注: リクエストパラメータをdemo(@Valid @Size(max = 5)String code)みたいに引数でバリデーション設定している場合はコントローラーとか機能テストじゃないと無理
#### とりあえず動かす
RequestパラメータのDTO
```java
@Getter
@Setter
public class DemoRequest {

    @NotNull
    @Size(max = 5)
    private String code;
}
```
テストコード
```java
public class DemoRequestTest {

    private Validator validator;

    @Before
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validatorTest_桁あふれ() {
        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setCode("123456");
        Set<ConstraintViolation<DemoRequest>> violationSet = validator.validate(demoRequest);
        assertThat(violationSet.size(), is(1));
        violationSet.forEach(violation -> {
            assertThat(violation.getConstraintDescriptor().getAnnotation(), instanceOf(Size.class));
        });
    }
}
```
#### 解説
* テストコード内で実際にvalidateしてその結果をテストしている
* Validatorをインスタンス化して対象のDTOをvalidateしている
* validateした結果はConstraintViolationのSetとして返却されるのでその内容をぐるぐる回してテストしている

## 機能テスト
TestRestTemplateを使ってAPIを実際に動かすテストを書いた
#### とりあえず動かす
* Controllerは上記のものを流用
* DbUnit周りも上記のものを流用
###### テストコード
```java

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(
        listeners = {
                TransactionalTestExecutionListener.class,
                DbUnitTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@DbUnitConfiguration(
        dataSetLoader = ReplacementCsvDataSetLoader.class
)
public class DemoApiFunctionalTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @DatabaseSetup(value = "/DemoRepository/findByCode/")
    @Test
    public void demoGetTest_正常系() {

        // 実行
        ResponseEntity actual = testRestTemplate.exchange(
                "/v1/demo?code=hoge", HttpMethod.GET, new HttpEntity<>(null, null), String.class);
        assertThat(actual.getBody().toString(), is("hello"));
        assertThat(actual.getStatusCode(), is(HttpStatus.OK));
    }
}
```
#### 解説
* TestRestTemplateを利用して実際にアプリケーションを実行してその返却値をテストした
* DBにテスト用のデータをいれたいのでDBUnitの設定をしてテストデータをいれた
* portをランダムにしたいので@SpringBootTestで設定した
* @TestExecutionのmergeModeでデフォルトのリスナーに追加している

## まとめ
* CSVを使ってDBのデータをセットアップするのは面倒だけど、基本用意されているものだけでできるので楽ちん
* TestRestTemplateがあるから機能テストもさっとできる。DBUnit使えばテストデータもぶっこめる
* Spring bootのテストなのかそうでないのかで書き方変わるから注意

# 参考にしたサイト
* [Spring bootの公式のテストの資料](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html)
* [SpringBoot/SpringMVCでRepositoryのテストを楽にするspring-test-dbunit/くらげになりたい](http://wannabe-jellyfish.hatenablog.com/entry/2016/05/22/123658)
* [Spring Test DBUnit](http://springtestdbunit.github.io/spring-test-dbunit/)
