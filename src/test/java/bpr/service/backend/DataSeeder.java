package bpr.service.backend;

import bpr.service.backend.models.entities.ProductEntity;
import bpr.service.backend.models.entities.TagEntity;
import bpr.service.backend.persistence.repository.productRepository.IProductRepository;
import bpr.service.backend.persistence.repository.tagRepository.ITagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class DataSeeder {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(DataSeeder.class, args);
        SomeService service = applicationContext.getBean(SomeService.class);
        service.init();
    }
}

@Service
class SomeService {

    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private ITagRepository tagRepository;

    private List<TagEntity> tags;


    private void createAllTags() {
        tags = (List<TagEntity>) tagRepository.findAll();

        TagEntity childrenTag = new TagEntity("Children");
        TagEntity modernTag = new TagEntity("Modern");
        TagEntity cozyTag = new TagEntity("Cozy");
        TagEntity classic = new TagEntity("Classic");
        TagEntity retroTag = new TagEntity("Retro");
        TagEntity tableLampTag = new TagEntity("Table");
        TagEntity floorTag = new TagEntity("Floor");
        TagEntity readingTag = new TagEntity("Reading");
        TagEntity ceilingTag = new TagEntity("Ceiling");
        TagEntity uplightTag = new TagEntity("Uplight");
        TagEntity darkTag = new TagEntity("Dark");
        TagEntity spotTag = new TagEntity("Spot");
        TagEntity glassTag = new TagEntity("Glass");

        if (!tags.stream().anyMatch(x -> tags.stream().anyMatch(y -> x.getTag().equals(y.getTag()))))
            tagRepository.saveAll(List.of(childrenTag, modernTag, cozyTag, classic, retroTag, tableLampTag, floorTag, readingTag, ceilingTag, uplightTag, darkTag, spotTag, glassTag));


        tags = (List<TagEntity>) tagRepository.findAll();
        System.out.println("Tags created");

    }

    public void init() {

        createAllTags();
        // Product: https://www.ikea.com/dk/da/p/upplyst-led-vaeglampe-sommerfugl-lysebla-60440341/
        TagEntity childrenTag = tagRepository.findTagEntityByTag("Children");
        TagEntity modernTag = tagRepository.findTagEntityByTag("Modern");
        TagEntity cozyTag = tagRepository.findTagEntityByTag("Cozy");
        List<TagEntity> upplystTags = new ArrayList<>() {{
            add(childrenTag);
            add(modernTag);
            add(cozyTag);
        }};
        ProductEntity upplyst = new ProductEntity();
        upplyst.setTags(upplystTags);
        upplyst.setName("Upplyst");
        upplyst.setNumber("60440341");
        upplyst.setImage("https://www.ikea.com/dk/da/images/products/upplyst-led-vaeglampe-sommerfugl-lysebla__0716795_pe731046_s5.jpg");

//        productRepository.save(upplyst);

        // Product: https://www.ikea.com/dk/da/p/arstid-bordlampe-messing-hvid-30321373/
        TagEntity classicTag = tagRepository.findTagEntityByTag("Classic");

        List<TagEntity> arstidTags = new ArrayList<>() {{
            add(classicTag);
            add(tagRepository.findTagEntityByTag("Cozy"));
        }};

        ProductEntity arstid = new ProductEntity();
        arstid.setTags(arstidTags);
        arstid.setName("aarstid");
        arstid.setNumber("30321373");
        arstid.setImage("https://www.ikea.com/dk/da/images/products/arstid-bordlampe-messing-hvid__0880725_pe617347_s5.jpg");

//        productRepository.save(arstid);
//
        // Product: https://www.ikea.com/dk/da/p/taernaby-bordlampe-antracit-60323894/
        TagEntity retroTag = tagRepository.findTagEntityByTag("Retro");
        TagEntity tableLampTag = tagRepository.findTagEntityByTag("Table");

        List<TagEntity> tarnabyTags = new ArrayList<>() {{
            add(retroTag);
            add(tableLampTag);
        }};

        ProductEntity tarnaby = new ProductEntity();
        tarnaby.setTags(tarnabyTags);
        tarnaby.setName("Tärnaby");
        tarnaby.setNumber("60323894");
        tarnaby.setImage("https://www.ikea.com/dk/da/images/products/taernaby-bordlampe-antracit__0811972_pe771891_s5.jpg");

//        productRepository.save(tarnaby);
//
        // Product: https://www.ikea.com/dk/da/p/askmuller-bordlampe-gragron-00492489/

        List<TagEntity> askmullerTags = new ArrayList<>(List.of(tagRepository.findTagEntityByTag("Modern"), tagRepository.findTagEntityByTag("Table")));

        ProductEntity askmuller = new ProductEntity();
        askmuller.setTags(askmullerTags);
        askmuller.setName("aaskmuller");
        askmuller.setNumber("00492489");
        askmuller.setImage("https://www.ikea.com/dk/da/images/products/askmuller-bordlampe-gragron__1043950_pe841841_s5.jpg");

//        productRepository.save(askmuller);

        // Product: https://www.ikea.com/dk/da/p/nymane-gulvlampe-med-3-spot-antracit-80477734/
        TagEntity floorTag = tagRepository.findTagEntityByTag("Floor");

        List<TagEntity> nymaneTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Modern"));
            add(floorTag);
        }};

        ProductEntity nymane = new ProductEntity();
        nymane.setTags(nymaneTags);
        nymane.setName("NYMaaNE");
        nymane.setNumber("80477734");
        nymane.setImage("https://www.ikea.com/dk/da/images/products/nymane-gulvlampe-med-3-spot-antracit__0810829_pe771429_s5.jpg");

//        productRepository.save(nymane);

        // Product: https://www.ikea.com/dk/da/p/ringsta-skaftet-gulvlampe-hvid-messing-s59385958/

        List<TagEntity> ringstaTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Classic"));
            add(tagRepository.findTagEntityByTag("Floor"));
        }};

        ProductEntity ringsta = new ProductEntity();
        ringsta.setTags(ringstaTags);
        ringsta.setName("RINGSTA");
        ringsta.setNumber("59385958");
        ringsta.setImage("https://www.ikea.com/dk/da/images/products/ringsta-skaftet-gulvlampe-hvid-messing__0785991_pe762840_s5.jpg");

//        productRepository.save(ringsta);

        // Product: https://www.ikea.com/dk/da/p/skurup-gulv-laeselampe-sort-20471117/
        TagEntity readingTag = tagRepository.findTagEntityByTag("Reading");

        List<TagEntity> skurupTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Retro"));
            add(tagRepository.findTagEntityByTag("Floor"));
            add(readingTag);
        }};

        ProductEntity skurup = new ProductEntity();
        skurup.setTags(skurupTags);
        skurup.setName("SKURUP");
        skurup.setNumber("20471117");
        skurup.setImage("https://www.ikea.com/dk/da/images/products/skurup-gulv-laeselampe-sort__0879772_pe700376_s5.jpg");

//        productRepository.save(skurup);

        // Product: https://www.ikea.com/dk/da/p/tagarp-gulvuplight-sort-hvid-20404095/
        TagEntity uplightTag = tagRepository.findTagEntityByTag("Uplight");

        List<TagEntity> tagarpTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Modern"));
            add(tagRepository.findTagEntityByTag("Floor"));
            add(uplightTag);
        }};

        ProductEntity tagarp = new ProductEntity();
        tagarp.setTags(tagarpTags);
        tagarp.setName("TaaGARP");
        tagarp.setNumber("20404095");
        tagarp.setImage("https://www.ikea.com/dk/da/images/products/tagarp-gulvuplight-sort-hvid__0810839_pe771437_s5.jpg");

//        productRepository.save(tagarp);

        // Product: https://www.ikea.com/dk/da/p/vaexjoe-loftlampe-beige-40394284/
        TagEntity ceilingTag = tagRepository.findTagEntityByTag("Ceiling");

        List<TagEntity> vaexjoeTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Retro"));
            add(ceilingTag);
        }};

        ProductEntity vaexjoe = new ProductEntity();
        vaexjoe.setTags(vaexjoeTags);
        vaexjoe.setName("VÄXJÖ");
        vaexjoe.setNumber("40394284");
        vaexjoe.setImage("https://www.ikea.com/dk/da/images/products/vaexjoe-loftlampe-beige__0881054_pe659755_s5.jpg");

//        productRepository.save(vaexjoe);

        // Product: https://www.ikea.com/dk/da/p/vindkast-loftlampe-hvid-20450520/

        List<TagEntity> vindkastTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Modern"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity vindkast = new ProductEntity();
        vindkast.setTags(vindkastTags);
        vindkast.setName("VÄXJÖ");
        vindkast.setNumber("20450520");
        vindkast.setImage("https://www.ikea.com/dk/da/images/products/vaexjoe-loftlampe-beige__0881054_pe659755_s5.jpg");

//        productRepository.save(vindkast);

        // Product: https://www.ikea.com/dk/da/p/hektar-loftlampe-morkegra-80390359/
        TagEntity darkTag = tagRepository.findTagEntityByTag("Dark");

        List<TagEntity> hektarTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Retro"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
            add(darkTag);
        }};

        ProductEntity hektar = new ProductEntity();
        hektar.setTags(hektarTags);
        hektar.setName("HEKTAR");
        hektar.setNumber("80390359");
        hektar.setImage("https://www.ikea.com/dk/da/images/products/hektar-loftlampe-morkegra__0880519_pe613970_s5.jpg");

//        productRepository.save(hektar);

        // Product: https://www.ikea.com/dk/da/p/solklint-plafond-messing-grat-klart-glas-40472031/
        TagEntity glassTag = tagRepository.findTagEntityByTag("Glass");

        List<TagEntity> solklintTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Retro"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
            add(glassTag);
        }};

        ProductEntity solklint = new ProductEntity();
        solklint.setTags(solklintTags);
        solklint.setName("SOLKLINT");
        solklint.setNumber("40472031");
        solklint.setImage("https://www.ikea.com/dk/da/images/products/solklint-plafond-messing-grat-klart-glas__0842294_pe778941_s5.jpg");

//        productRepository.save(solklint);

        // Product: https://www.ikea.com/dk/da/p/arstid-plafond-hvid-90176047/

        List<TagEntity> aarstidTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Cozy"));
            add(tagRepository.findTagEntityByTag("Retro"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity aarstid = new ProductEntity();
        aarstid.setTags(aarstidTags);
        aarstid.setName("aaRSTID");
        aarstid.setNumber("90176047");
        aarstid.setImage("https://www.ikea.com/dk/da/images/products/arstid-plafond-hvid__0879976_pe618549_s5.jpg");

//        productRepository.save(aarstid);

        // Product: https://www.ikea.com/dk/da/p/hyby-plafond-hvid-90347389/

        List<TagEntity> hybyTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Glass"));
            add(tagRepository.findTagEntityByTag("Modern"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity hyby = new ProductEntity();
        hyby.setTags(hybyTags);
        hyby.setName("HYBY");
        hyby.setNumber("90347389");
        hyby.setImage("https://www.ikea.com/dk/da/images/products/hyby-plafond-hvid__1052855_pe846466_s5.jpg");

//        productRepository.save(hyby);

        // Product: https://www.ikea.com/dk/da/p/dejsa-loftlampe-med-3-lamper-forkromet-opalhvid-glas-00430769/

        List<TagEntity> dejsaTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Glass"));
            add(tagRepository.findTagEntityByTag("Classic"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity dejsa = new ProductEntity();
        dejsa.setTags(dejsaTags);
        dejsa.setName("DEJSA");
        dejsa.setNumber("00430769");
        dejsa.setImage("https://www.ikea.com/dk/da/images/products/dejsa-loftlampe-med-3-lamper-forkromet-opalhvid-glas__0967525_pe810177_s5.jpg");

//        productRepository.save(dejsa);

        // Product: https://www.ikea.com/dk/da/p/skurup-loftskinne-3-spot-sort-10395925/
        TagEntity spotTag = tagRepository.findTagEntityByTag("Spot");

        List<TagEntity> skurupspotTags = new ArrayList<>() {{
            add(spotTag);
            add(tagRepository.findTagEntityByTag("Modern"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity skurupspot = new ProductEntity();
        skurupspot.setTags(skurupspotTags);
        skurupspot.setName("SKURUP");
        skurupspot.setNumber("10395925");
        skurupspot.setImage("https://www.ikea.com/dk/da/images/products/skurup-loftskinne-3-spot-sort__0751052_pe746893_s5.jpg");

//        productRepository.save(skurupspot);

        // Product: https://www.ikea.com/dk/da/p/nymane-loftspot-med-4-spots-antracit-80415086/

        List<TagEntity> nymaaneTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Spot"));
            add(tagRepository.findTagEntityByTag("Classic"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity nymaane = new ProductEntity();
        nymaane.setTags(nymaaneTags);
        nymaane.setName("NYMaaNE");
        nymaane.setNumber("80415086");
        nymaane.setImage("https://www.ikea.com/dk/da/images/products/nymane-loftspot-med-4-spots-antracit__0879867_pe707508_s5.jpg");

//        productRepository.save(nymaane);

        // Product: https://www.ikea.com/dk/da/p/barometer-loftskinne-5-spot-messingfarvet-60364634/

        List<TagEntity> barometerTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Spot"));
            add(tagRepository.findTagEntityByTag("Retro"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity barometer = new ProductEntity();
        barometer.setTags(barometerTags);
        barometer.setName("BAROMETER");
        barometer.setNumber("60364634");
        barometer.setImage("https://www.ikea.com/dk/da/images/products/barometer-loftskinne-5-spot-messingfarvet__0880024_pe659744_s5.jpg");

//        productRepository.save(barometer);

        // Product: https://www.ikea.com/dk/da/p/hektar-loftskinne-3-spot-morkegra-50297485/

        List<TagEntity> hektarspotTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Spot"));
            add(tagRepository.findTagEntityByTag("Retro"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity hektarspot = new ProductEntity();
        hektarspot.setTags(hektarspotTags);
        hektarspot.setName("HEKTAR");
        hektarspot.setNumber("50297485");
        hektarspot.setImage("https://www.ikea.com/dk/da/images/products/hektar-loftskinne-3-spot-morkegra__0880187_pe671281_s5.jpg");

//        productRepository.save(hektarspot);

        // Product: https://www.ikea.com/dk/da/p/ranarp-loftskinne-3-spot-sort-70396390/

        List<TagEntity> ranarpTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Spot"));
            add(tagRepository.findTagEntityByTag("Classic"));
            add(tagRepository.findTagEntityByTag("Ceiling"));
        }};

        ProductEntity ranarp = new ProductEntity();
        ranarp.setTags(ranarpTags);
        ranarp.setName("RANARP");
        ranarp.setNumber("70396390");
        ranarp.setImage("https://www.ikea.com/dk/da/images/products/ranarp-loftskinne-3-spot-sort__0879805_pe711246_s5.jpg");

//        productRepository.save(ranarp);

        // Product: https://www.ikea.com/dk/da/p/naevlinge-led-klemspot-hvid-70449888/

        List<TagEntity> naevlingeTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Spot"));
            add(tagRepository.findTagEntityByTag("Modern"));
            add(tagRepository.findTagEntityByTag("Table"));
        }};

        ProductEntity naevlinge = new ProductEntity();
        naevlinge.setTags(naevlingeTags);
        naevlinge.setName("NÄVLINGE");
        naevlinge.setNumber("70449888");
        naevlinge.setImage("https://www.ikea.com/dk/da/images/products/naevlinge-led-klemspot-hvid__0726711_pe735399_s5.jpg");

//        productRepository.save(naevlinge);

        // Product: https://www.ikea.com/dk/da/p/ranarp-skrivebordslampe-sort-50331385/

        List<TagEntity> ranarpdeskTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Cozy"));
            add(tagRepository.findTagEntityByTag("Classic"));
            add(tagRepository.findTagEntityByTag("Table"));
            add(tagRepository.findTagEntityByTag("Reading"));
        }};

        ProductEntity ranarpdesk = new ProductEntity();
        ranarpdesk.setTags(ranarpdeskTags);
        ranarpdesk.setName("NÄVLINGE");
        ranarpdesk.setNumber("50331385");
        ranarpdesk.setImage("https://www.ikea.com/dk/da/images/products/naevlinge-led-klemspot-hvid__0726711_pe735399_s5.jpg");

//        productRepository.save(ranarpdesk);

        // Product: https://www.ikea.com/dk/da/p/tertial-skrivebordslampe-lysebla-20504288/

        List<TagEntity> tertialTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Reading"));
            add(tagRepository.findTagEntityByTag("Retro"));
            add(tagRepository.findTagEntityByTag("Table"));
        }};

        ProductEntity tertial = new ProductEntity();
        tertial.setTags(tertialTags);
        tertial.setName("TERTIAL");
        tertial.setNumber("20504288");
        tertial.setImage("https://www.ikea.com/dk/da/images/products/tertial-skrivebordslampe-lysebla__0957570_pe822320_s5.jpg");

//        productRepository.save(tertial);

        // Product: https://www.ikea.com/dk/da/p/trollbo-loftlampe-lysegron-80346875/

        List<TagEntity> trollboTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Ceiling"));
            add(tagRepository.findTagEntityByTag("Modern"));
            add(tagRepository.findTagEntityByTag("Children"));
        }};

        ProductEntity trollbo = new ProductEntity();
        trollbo.setTags(trollboTags);
        trollbo.setName("TROLLBO");
        trollbo.setNumber("80346875");
        trollbo.setImage("https://www.ikea.com/dk/da/images/products/trollbo-loftlampe-lysegron__0883543_pe712943_s5.jpg");

//        productRepository.save(trollbo);

        // Product: https://www.ikea.com/dk/da/p/aengarna-led-bordlampe-hundemonster-50440855/

        List<TagEntity> aengarnaTags = new ArrayList<>() {{
            add(tagRepository.findTagEntityByTag("Table"));
            add(tagRepository.findTagEntityByTag("Modern"));
            add(tagRepository.findTagEntityByTag("Children"));
        }};

        ProductEntity aengarna = new ProductEntity();
        aengarna.setTags(aengarnaTags);
        aengarna.setName("ÄNGARNA");
        aengarna.setNumber("50440855");
        aengarna.setImage("https://www.ikea.com/dk/da/images/products/aengarna-led-bordlampe-hundemonster__0754605_pe747977_s5.jpg");

//        productRepository.save(aengarna);
        productRepository.saveAll(List.of(aengarna, trollbo, tertial, ranarpdesk, naevlinge, ranarp, hektarspot, barometer, nymaane, skurupspot, dejsa, hyby, aarstid, solklint, hektar, vindkast, vaexjoe, tagarp, skurup, ringsta, nymane, upplyst, arstid, tarnaby, askmuller));
        System.out.println("created");

    }
}