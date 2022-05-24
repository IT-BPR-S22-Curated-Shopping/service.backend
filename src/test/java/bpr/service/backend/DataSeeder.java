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
        upplyst.setPrice(149);
        upplyst.setCaption("LED-væglampe, sommerfugl lyseblå");
        upplyst.setDescription("Børn elsker det muntre design og det hyggelige lys, lampen giver, når den er tændt. Vores belysning til børn gennemgår nogle af verdens strengeste sikkerhedstest, så du kan være sikker på, at dit barn ikke kommer til skade.");

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
        arstid.setPrice(199);
        arstid.setCaption("Bordlampe, messing/hvid");
        arstid.setDescription("Det er en af vores mest populære lampeserier, og det kan vi godt forstå – lamperne har et tidløst design, der er nemt at indrette med. Kombiner flere lamper fra serien, og skab blød og behagelig belysning og et sammenhængende udtryk.");

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
        tarnaby.setPrice(189);
        tarnaby.setCaption("Bordlampe, antracit");
        tarnaby.setDescription("Inspireret af traditionelle petroleumslamper og blød og varm stemningsbelysning. Den synlige pære ligner en levende flamme, og du kan indstille lysstyrken med den indbyggede lysdæmper.");

//        productRepository.save(tarnaby);
//
        // Product: https://www.ikea.com/dk/da/p/askmuller-bordlampe-gragron-00492489/

        List<TagEntity> askmullerTags = new ArrayList<>(List.of(tagRepository.findTagEntityByTag("Modern"), tagRepository.findTagEntityByTag("Table")));

        ProductEntity askmuller = new ProductEntity();
        askmuller.setTags(askmullerTags);
        askmuller.setName("aaskmuller");
        askmuller.setNumber("00492489");
        askmuller.setImage("https://www.ikea.com/dk/da/images/products/askmuller-bordlampe-gragron__1043950_pe841841_s5.jpg");
        askmuller.setPrice(149);
        askmuller.setCaption("Bordlampe, grågrøn, 24 cm");
        askmuller.setDescription("En moderne version af en traditionel petroleumslampe. Den synlige pære ligner en rigtig flamme på et stearinlys og spreder hyggelig og varm stemningsbelysning, som du kan tilpasse efter behov med den indbyggede dæmper.");

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
        nymane.setPrice(449);
        nymane.setCaption("Gulvlampe med 3 spot, antracit");
        nymane.setDescription("Strålende, tidløst design NYMÅNE lamper har både et markant design og matcher de fleste former for indretning. Hvorfor ikke kombinere mange forskellige lamper og få en sammenhængende stil derhjemme?");

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
        ringsta.setPrice(389);
        ringsta.setCaption("Gulvlampe, hvid/messing");
        ringsta.setDescription("Stilren messingfarvet lampefod med en skærm i hvid tekstil, der spreder et ensartet og dekorativt lys i rummet, når lampen er tændt. Er du vild med stilen? Du kan indrette med flere lamper fra samme serie.");

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
        skurup.setPrice(429);
        skurup.setCaption("Gulv-/læselampe, sort");
        skurup.setDescription("Metal, en gedigen konstruktion og et tidløst design – du kan glæde dig over SKURUP lampeserie i mange år. Lamperne fås i forskellige versioner og er nemme at indstille. Det gør serien praktisk og fleksibel at bruge i hele hjemmet.");

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
        tagarp.setPrice(69);
        tagarp.setCaption("Gulvuplight, sort/hvid");
        tagarp.setDescription("TÅGARP gulvuplight lyser opad og giver et spredt og behageligt lys i rummet. Dele af lampen er fremstillet af genanvendt plast – det er godt for miljøet, og prisen er fantastisk.");

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
        vaexjoe.setPrice(359);
        vaexjoe.setCaption("Loftlampe, beige");
        vaexjoe.setDescription("VÄXJÖ loftlampe er fremstillet af glat aluminium og klassiske linjer. Den giver et blødt lys, der ikke blænder, og lampen fylder kun lidt under transport – det er godt for både klimaet og din pengepung.");

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
        vindkast.setPrice(269);
        vindkast.setCaption("Loftlampe, hvid");
        vindkast.setDescription("Denne loftlampe ligner en let og svævende sky af genanvendt polyester, og lampens bløde skær skaber en hyggelig stemning. Perfekt at hænge i soveværelset eller over sofabordet.");

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
        hektar.setPrice(179);
        hektar.setCaption("Loftlampe, mørkegrå");
        hektar.setDescription("Formen er enkel, i overstørrelse og af metal og er inspireret af gamle lamper fra f.eks. fabrikker og teatre. Flere HEKTAR lamper, der bruges sammen, understøtter forskellige aktiviteter og giver rummet et sammenhængende og rustikt udtryk.");

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
        solklint.setPrice(179);
        solklint.setCaption("Plafond, messing/gråt klart glas");
        solklint.setDescription("Lamperne i SOLKLINT serien ligner små juveler med skinnende messing og gråt glas og spreder blød stemningsbelysning, der skaber spændende skygger på vægge og lofter – uanset hvor du placerer lamperne.");

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
        aarstid.setPrice(239);
        aarstid.setCaption("Plafond, hvid");
        aarstid.setDescription("Det er en af vores mest populære lampeserier, og det kan vi godt forstå – lamperne har et tidløst design, der er nemt at indrette med. Kombiner flere lamper fra serien, og skab blød og behagelig belysning og et sammenhængende udtryk.");

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
        hyby.setPrice(99);
        hyby.setCaption("Plafond, hvid");
        hyby.setDescription("Denne dekorative loftlampe med en bølget skærm af hvidt frostet glas spreder lyset over et stort område og passer både i entreen, køkkenet og soveværelset.");

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
        dejsa.setPrice(649);
        dejsa.setCaption("Loftlampe med 3 lamper, forkromet/opalhvid glas");
        dejsa.setDescription("Alle lamperne i DEJSA serien har detaljer af krom og skærme med bløde former fremstillet af mundblæst opalglas. Uanset hvilken skærm du vælger, får du et blødt og dæmpet lys, der skaber en hyggelig stemning i rummet.");

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
        skurupspot.setPrice(179);
        skurupspot.setCaption("Loftskinne, 3 spot, sort");
        skurupspot.setDescription("Metal, en gedigen konstruktion og et tidløst design – du kan glæde dig over SKURUP lampeserie i mange år. Lamperne fås i forskellige versioner og er nemme at indstille. Det gør serien praktisk og fleksibel at bruge i hele hjemmet.");

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
        nymaane.setPrice(349);
        nymaane.setCaption("Loftspot med 4 spots, antracit");
        nymaane.setDescription("Strålende, tidløst design NYMÅNE lamper har både et markant design og matcher de fleste former for indretning. Hvorfor ikke kombinere mange forskellige lamper og få en sammenhængende stil derhjemme?");

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
        barometer.setPrice(449);
        barometer.setCaption("Loftskinne, 5 spot, messingfarvet");
        barometer.setDescription("Denne messingfarvede loftskinne har 5 spot, der er nemme at pege i præcis den retning, du ønsker lyset. Den passer godt i både entreen, køkkenet og stuen.");

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
        hektarspot.setPrice(179);
        hektarspot.setCaption("Loftskinne, 3 spot, mørkegrå");
        hektarspot.setDescription("Formen er enkel, i overstørrelse og af metal og er inspireret af gamle lamper fra f.eks. fabrikker og teatre. Flere HEKTAR lamper, der bruges sammen, understøtter forskellige aktiviteter og giver rummet et sammenhængende og rustikt udtryk.");

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
        ranarp.setPrice(269);
        ranarp.setCaption("Loftskinne, 3 spot, sort");
        ranarp.setDescription("Med sine håndværksmæssige detaljer af stål minder RANARP loftskinne om gamle dage og lyser op, præcis hvor du ønsker det, fordi de 3 sorte spot kan indstilles hver for sig.");

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
        naevlinge.setPrice(119);
        naevlinge.setCaption("LED-klemspot, hvid");
        naevlinge.setDescription("NÄVLINGE serien indeholder lamper, der opfylder de fleste behov. Det er smarte lamper i et design, der matcher den øvrige indretning, og som er nemme at bruge overalt i hjemmet – og som giver et godt lys, der ikke blænder.");

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
        ranarpdesk.setPrice(279);
        ranarpdesk.setCaption("Skrivebordslampe, sort");
        ranarpdesk.setDescription("RANARP lamper minder om fortiden og er designet med detaljer som stålsamlingerne og den stribede ledning af tekstil. Gulv- og skrivebordslamperne er tunge og meget stabile, men kan stadigvæk tilpasses.");

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
        tertial.setPrice(89);
        tertial.setCaption("Skrivebordslampe, lyseblå");
        tertial.setDescription("TERTIAL arbejdslampe blev lanceret i sortimentet i 1998. Det klassiske design med stål og indstillelig arm og lampehoved gør lampen til det perfekte valg, hvis du ønsker dig et fleksibelt og effektivt læselys.");

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
        trollbo.setPrice(199);
        trollbo.setCaption("Loftlampe, lysegrøn");
        trollbo.setDescription("Tænk, hvis du havde et strålende cirkustelt derhjemme! Loftlampen er fremstillet af genanvendt plast, og emballagen kan genbruges som farvebog. Testet i henhold til nogle af verdens strengeste sikkerhedsstandarder.");

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
        aengarna.setPrice(159);
        aengarna.setCaption("LED-bordlampe, hundemønster");
        aengarna.setDescription("Denne vågne og kvikke hund holder gerne vagt ved vinduet, på en hylde eller ved sengen. En tryg ven at have ved din side. Testet i henhold til nogle af verdens strengeste sikkerhedskrav.");

//        productRepository.save(aengarna);
        productRepository.saveAll(List.of(aengarna, trollbo, tertial, ranarpdesk, naevlinge, ranarp, hektarspot, barometer, nymaane, skurupspot, dejsa, hyby, aarstid, solklint, hektar, vindkast, vaexjoe, tagarp, skurup, ringsta, nymane, upplyst, arstid, tarnaby, askmuller));
        System.out.println("created");

    }
}