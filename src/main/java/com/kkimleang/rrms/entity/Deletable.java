package com.kkimleang.rrms.entity;

import java.time.*;
import java.util.*;

public interface Deletable {
    UUID getDeletedBy();

    Instant getDeletedAt();
}
