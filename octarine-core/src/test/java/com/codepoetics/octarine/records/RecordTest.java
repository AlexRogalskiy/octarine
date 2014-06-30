package com.codepoetics.octarine.records;

import com.codepoetics.octarine.lenses.Lens;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordTest {

    private static final Key<String> displayName = Key.named("displayName");
    private static final Key<String> name = Key.named("name", displayName.of("person_address"));
    private static final Key<Integer> age = Key.named("age");
    private static final RecordKey address = RecordKey.named("address");
    private static final ListKey<String> addressLines = ListKey.named("addressLines");
    private static final Key<String> notAppearingInThisRecord = Key.named("not appearing in this record");
    private static final Record testRecord = Record.of(
            name.of("Arthur Putey"),
            age.of(43),
            address.of(
                    addressLines.of("23 Acacia Avenue", "Sunderland", "VB6 5UX")
            )
    );

    @Test
    public void
    keys_are_lenses() {
        Lens<Record, String> secondLineOfAddress =
                address.assertPresent().join(addressLines.assertPresent()).join(Lens.intoPVector(1));

        assertThat(secondLineOfAddress.get(testRecord), equalTo("Sunderland"));
        assertThat(secondLineOfAddress.set(testRecord, "Cirencester"),
                equalTo(Record.of(
                        name.of("Arthur Putey"),
                        age.of(43),
                        address.of(Record.of(
                                addressLines.of("23 Acacia Avenue", "Cirencester", "VB6 5UX")
                        ))
                ))
        );
    }

    @Test
    public void
    keys_are_extractors() {
        assertThat(address.test(testRecord), equalTo(true));
        assertThat(notAppearingInThisRecord.test(testRecord), equalTo(false));

        assertThat(name.extract(testRecord), equalTo("Arthur Putey"));
        assertThat(name.get(testRecord), equalTo(Optional.of("Arthur Putey")));
        assertThat(notAppearingInThisRecord.get(testRecord), equalTo(Optional.empty()));
    }

}
