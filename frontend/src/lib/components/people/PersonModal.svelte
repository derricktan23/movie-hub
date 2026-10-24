<script lang="ts">
  import Modal from '../common/Modal.svelte'
  import Button from '../common/Button.svelte'
  import TextInput from '../common/TextInput.svelte'
  import { peopleStore } from '../../stores/peopleStore'
  import type { CreatePersonInput, PersonRole } from '../../types/person'

  interface Props {
    open: boolean
    onclose: () => void
  }

  let { open = false, onclose }: Props = $props()

  let name = $state('')
  let selectedRole = $state<string>('ACTOR')
  let customRole = $state('')
  let isCustom = $state(false)
  let submitting = $state(false)
  let nameError = $state('')

  const PRESET_ROLES: PersonRole[] = ['ACTOR', 'DIRECTOR', 'PRODUCER', 'COMPOSER', 'CREW']

  function resetForm() {
    name = ''
    selectedRole = 'ACTOR'
    customRole = ''
    isCustom = false
    nameError = ''
  }

  async function handleSubmit(e: SubmitEvent) {
    e.preventDefault()

    if (!name.trim()) {
      nameError = 'Person name is required'
      return
    }

    const finalRole = isCustom ? customRole.trim().toUpperCase() : selectedRole
    if (!finalRole) {
      nameError = 'Role cannot be empty'
      return
    }

    submitting = true
    const input: CreatePersonInput = {
      name: name.trim(),
      role: finalRole
    }

    const created = await peopleStore.create(input)
    submitting = false

    if (created) {
      resetForm()
      onclose()
    }
  }
</script>

<Modal {open} title="Add Person to Directory" onclose={() => { resetForm(); onclose(); }}>
  <form id="person-form" onsubmit={handleSubmit} class="form-layout">
    <TextInput
      id="person-name"
      label="Full Name"
      placeholder="e.g. Christopher Nolan, Leonardo DiCaprio"
      required
      bind:value={name}
      error={nameError}
      oninput={() => nameError = ''}
    />

    <div class="form-group">
      <label for="role-btn-group">Primary Professional Role *</label>
      <div id="role-btn-group" class="role-grid">
        {#each PRESET_ROLES as role}
          <button
            type="button"
            class="role-choice {(!isCustom && selectedRole === role) ? 'active' : ''}"
            onclick={() => { isCustom = false; selectedRole = role; }}
          >
            {role}
          </button>
        {/each}
        <button
          type="button"
          class="role-choice {isCustom ? 'active' : ''}"
          onclick={() => isCustom = true}
        >
          Other...
        </button>
      </div>

      {#if isCustom}
        <TextInput
          id="custom-role"
          label="Custom Role Name"
          placeholder="e.g. SCREENWRITER, CINEMATOGRAPHER"
          bind:value={customRole}
        />
      {/if}
    </div>
  </form>

  {#snippet footer()}
    <Button variant="ghost" onclick={() => { resetForm(); onclose(); }} disabled={submitting}>
      Cancel
    </Button>
    <Button variant="primary" type="submit" form="person-form" loading={submitting}>
      Add to Registry
    </Button>
  {/snippet}
</Modal>

<style>
  .form-layout {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  label {
    font-size: 0.8125rem;
    font-weight: 600;
    color: var(--text-primary);
  }

  .role-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }

  .role-choice {
    padding: 10px 8px;
    background: #F8FAFC;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-md);
    color: var(--text-secondary);
    font-size: 0.8125rem;
    font-weight: 600;
    font-family: inherit;
    cursor: pointer;
    transition: all var(--transition-fast);
    text-align: center;
  }

  .role-choice:hover {
    background: #F1F5F9;
    color: var(--text-primary);
  }

  .role-choice.active {
    background: #EFF6FF;
    border-color: var(--accent-primary);
    color: var(--accent-primary);
    box-shadow: 0 1px 3px rgba(37, 99, 235, 0.1);
  }
</style>
