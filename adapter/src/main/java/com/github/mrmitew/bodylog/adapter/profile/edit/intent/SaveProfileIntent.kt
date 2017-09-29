package com.github.mrmitew.bodylog.adapter.profile.edit.intent

import com.github.mrmitew.bodylog.adapter.common.model.ViewIntent
import com.github.mrmitew.bodylog.domain.repository.entity.Profile


class SaveProfileIntent(val profile: Profile) : ViewIntent()
