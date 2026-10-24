<script lang="ts">
  interface Props {
    id: string
    label: string
    value: string | number | undefined
    type?: string
    placeholder?: string
    error?: string
    required?: boolean
    min?: number
    max?: number
    oninput?: (value: string) => void
  }

  let {
    id,
    label,
    value = $bindable(),
    type = 'text',
    placeholder = '',
    error = '',
    required = false,
    min,
    max,
    oninput
  }: Props = $props()

  function handleInput(e: Event) {
    const target = e.target as HTMLInputElement
    value = type === 'number' ? (target.value === '' ? undefined : Number(target.value)) : target.value
    oninput?.(target.value)
  }
</script>

<div class="form-group">
  <label for={id}>
    {label}
    {#if required}
      <span class="required-star">*</span>
    {/if}
  </label>
  <input
    {id}
    {type}
    {placeholder}
    {min}
    {max}
    value={value ?? ''}
    oninput={handleInput}
    class="form-input {error ? 'has-error' : ''}"
  />
  {#if error}
    <span class="error-msg">{error}</span>
  {/if}
</div>

<style>
  .form-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
    width: 100%;
  }

  label {
    font-size: 0.8125rem;
    font-weight: 600;
    color: var(--text-primary);
  }

  .required-star {
    color: var(--danger);
    margin-left: 2px;
  }

  .form-input {
    width: 100%;
    padding: 10px 14px;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-md);
    color: var(--text-primary);
    font-size: 0.875rem;
    font-family: inherit;
    outline: none;
    transition: all var(--transition-fast);
  }

  .form-input:focus {
    border-color: var(--border-focus);
    box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
  }

  .form-input.has-error {
    border-color: var(--danger);
  }

  .error-msg {
    font-size: 0.75rem;
    color: var(--danger);
  }
</style>
