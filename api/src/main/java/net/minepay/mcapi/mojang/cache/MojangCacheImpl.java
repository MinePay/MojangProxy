package net.minepay.mcapi.mojang.cache;

import net.minepay.mcapi.configuration.CacheConfiguration;
import net.minepay.mcapi.mojang.ProfileName;
import net.minepay.mcapi.mojang.Profile;
import net.minepay.mcapi.mojang.ProfileNameChange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides a caching implementation.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 * @todo Benchmark Redis' pipeline feature (custom serialization code is required)
 */
@Repository
public class MojangCacheImpl implements MojangCache {
    private final CacheConfiguration cacheConfiguration;
    private final RedisTemplate<String, Profile> profileRedisTemplate;
    private final RedisTemplate<String, ProfileName> identifierRedisTemplate;
    private final RedisTemplate<String, List<ProfileNameChange>> nameHistoryRedisTemplate;

    private ValueOperations<String, Profile> profileValueOperations;
    private ValueOperations<String, ProfileName> identifierValueOperations;
    private ValueOperations<String, List<ProfileNameChange>> nameHistoryValueOperations;

    @Autowired
    public MojangCacheImpl(@Nonnull CacheConfiguration cacheConfiguration, @Nonnull RedisTemplate<String, Profile> profileRedisTemplate, @Nonnull RedisTemplate<String, ProfileName> identifierRedisTemplate, @Nonnull RedisTemplate<String, List<ProfileNameChange>> nameHistoryRedisTemplate) {
        this.cacheConfiguration = cacheConfiguration;
        this.profileRedisTemplate = profileRedisTemplate;
        this.identifierRedisTemplate = identifierRedisTemplate;
        this.nameHistoryRedisTemplate = nameHistoryRedisTemplate;

        // TODO: Merging these might be sane
        this.profileValueOperations = profileRedisTemplate.opsForValue();
        this.identifierValueOperations = identifierRedisTemplate.opsForValue();
        this.nameHistoryValueOperations = nameHistoryRedisTemplate.opsForValue();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    @Transactional("profileRedisTemplate")
    public Profile findProfile(@Nonnull String identifier) {
        return this.profileValueOperations.get("profile:" + identifier.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    @Transactional("profileRedisTemplate")
    public void saveProfile(@Nonnull Profile profile) {
        if (this.profileValueOperations.setIfAbsent("profile:" + profile.getId().toLowerCase(), profile) && this.cacheConfiguration.getProfileCacheTime() != 0) {
            this.profileRedisTemplate.expire("profile:" + profile.getId().toLowerCase(), this.cacheConfiguration.getProfileCacheTime(), TimeUnit.SECONDS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    @Transactional("identifierRedisTemplate")
    public ProfileName findIdentifier(@Nonnull String name) {
        return this.identifierValueOperations.get("name:" + name.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    @Transactional("identifierRedisTemplate")
    public ProfileName findIdentifier(@Nonnull String name, @Nonnull Instant timestamp) {
        return this.identifierValueOperations.get("name:" + timestamp.getEpochSecond() + ":" + name.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    @Transactional("identifierRedisTemplate")
    public void saveIdentifier(@Nonnull ProfileName name, @Nonnull Instant timestamp) {
        if (this.identifierValueOperations.setIfAbsent("name:" + timestamp.getEpochSecond() + ":" + name.getName().toLowerCase(), name)) {
            this.identifierRedisTemplate.expire("name:" + timestamp.getEpochSecond() + ":" + name.getName().toLowerCase(), this.cacheConfiguration.getNameCacheTime(), TimeUnit.SECONDS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    @Transactional("identifierRedisTemplate")
    public void saveIdentifier(@Nonnull ProfileName name) {
        if (this.identifierValueOperations.setIfAbsent("name:" + name.getName().toLowerCase(), name)) {
            this.identifierRedisTemplate.expire("name:" + name.getName().toLowerCase(), this.cacheConfiguration.getNameCacheTime(), TimeUnit.SECONDS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    @Transactional("nameHistoryRedisTemplate")
    public List<ProfileNameChange> findNameHistory(@Nonnull String identifier) {
        return this.nameHistoryValueOperations.get("name_history:" + identifier.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    @Transactional("nameHistoryRedisTemplate")
    public void saveNameHistory(@Nonnull String identifier, @Nonnull List<ProfileNameChange> nameChanges) {
        if (this.nameHistoryValueOperations.setIfAbsent("name_history:" + identifier.toLowerCase(), nameChanges)) {
            this.nameHistoryRedisTemplate.expire("name_history:" + identifier.toLowerCase(), this.cacheConfiguration.getHistoryCacheTime(), TimeUnit.SECONDS);
        }
    }
}
