<script lang="ts">
  import type { Person } from '../../types/person'
  import Badge from '../common/Badge.svelte'
  import DeleteConfirmModal from '../common/DeleteConfirmModal.svelte'
  import EmptyState from '../common/EmptyState.svelte'
  import Avatar from '../common/Avatar.svelte'
  import { peopleStore } from '../../stores/peopleStore'
  import { searchQuery } from '../../stores/searchStore'
  import { Trash2, Users } from '@lucide/svelte'

  interface Props {
    people: Person[]
    loading: boolean
    onAddPerson: () => void
  }

  let { people = [], loading = false, onAddPerson }: Props = $props()

  let selectedPersonToDelete = $state<Person | null>(null)
  let deleteModalOpen = $state(false)
  let deleteLoading = $state(false)

  function handleDeleteClick(person: Person) {
    selectedPersonToDelete = person
    deleteModalOpen = true
  }

  async function handleConfirmDelete() {
    if (!selectedPersonToDelete?.id) return
    deleteLoading = true
    await peopleStore.remove(selectedPersonToDelete.id)
    deleteLoading = false
    deleteModalOpen = false
    selectedPersonToDelete = null
  }

  function handleCloseDeleteModal() {
    deleteModalOpen = false
    selectedPersonToDelete = null
  }
</script>

{#if loading && people.length === 0}
  <div class="table-skeleton glass-panel">
    {#each Array(5) as _}
      <div class="skeleton-row">
        <div class="skeleton-cell cell-avatar"></div>
        <div class="skeleton-cell cell-name"></div>
        <div class="skeleton-cell cell-role"></div>
        <div class="skeleton-cell cell-action"></div>
      </div>
    {/each}
  </div>
{:else if people.length === 0}
  <EmptyState
    isSearch={Boolean($searchQuery)}
    searchQuery={$searchQuery}
    icon={Users}
    title={$searchQuery ? 'No People Found' : 'People Directory is Empty'}
    description={$searchQuery ? '' : 'Register cast, directors, and creators to link them with catalog movies.'}
    actionText="Add First Person"
    onAction={onAddPerson}
    onClearSearch={() => searchQuery.set('')}
  />
{:else}
  <div class="table-container glass-panel animate-fade-in">
    <table class="enterprise-table">
      <thead>
        <tr>
          <th class="th-person">Person</th>
          <th class="th-role">Primary Role</th>
          <th class="th-id">System Identifier</th>
          <th class="th-actions">Actions</th>
        </tr>
      </thead>
      <tbody>
        {#each people as person (person.id)}
          <tr class="table-row">
            <td class="td-person">
              <Avatar name={person.name} size="md" />
              <span class="person-full-name">{person.name}</span>
            </td>
            <td class="td-role">
              <Badge role={person.role} />
            </td>
            <td class="td-id">
              <code class="uuid-code">{person.id}</code>
            </td>
            <td class="td-actions">
              <button
                type="button"
                class="row-delete-btn"
                onclick={() => handleDeleteClick(person)}
                aria-label="Delete person"
              >
                <Trash2 size={15} />
              </button>
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  </div>
{/if}

{#if selectedPersonToDelete}
  <DeleteConfirmModal
    open={deleteModalOpen}
    title="Delete Person"
    itemName={selectedPersonToDelete.name}
    loading={deleteLoading}
    onclose={handleCloseDeleteModal}
    onconfirm={handleConfirmDelete}
  />
{/if}

<style>
  .table-container {
    overflow-x: auto;
    background: #FFFFFF;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
  }

  .enterprise-table {
    width: 100%;
    border-collapse: collapse;
    text-align: left;
    font-size: 0.875rem;
  }

  thead tr {
    background: #F8FAFC;
    border-bottom: 1px solid var(--border-subtle);
  }

  th {
    padding: 14px 20px;
    font-weight: 600;
    color: var(--text-secondary);
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .table-row {
    border-bottom: 1px solid var(--border-subtle);
    transition: background var(--transition-fast);
  }

  .table-row:hover {
    background: #F8FAFC;
  }

  .table-row:last-child {
    border-bottom: none;
  }

  td {
    padding: 16px 20px;
    vertical-align: middle;
  }

  .td-person {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .person-full-name {
    font-weight: 600;
    color: var(--text-primary);
  }

  .uuid-code {
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
    font-size: 0.75rem;
    color: var(--text-secondary);
    background: #F1F5F9;
    padding: 3px 8px;
    border-radius: var(--radius-sm);
    border: 1px solid #E2E8F0;
  }

  .td-actions {
    text-align: right;
  }

  .th-actions {
    text-align: right;
  }

  .row-delete-btn {
    background: transparent;
    border: 1px solid transparent;
    color: var(--text-muted);
    cursor: pointer;
    padding: 6px;
    border-radius: var(--radius-sm);
    display: inline-flex;
    align-items: center;
    justify-content: center;
    transition: all var(--transition-fast);
  }

  .row-delete-btn:hover {
    background: var(--danger-bg);
    color: var(--danger-hover);
    border-color: #FECACA;
  }

  .table-skeleton {
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .skeleton-row {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 12px;
  }

  .skeleton-cell {
    background: #F1F5F9;
    border-radius: var(--radius-sm);
    animation: pulse 1.5s infinite;
    height: 16px;
  }

  .cell-avatar { width: 36px; height: 36px; border-radius: 50%; }
  .cell-name { width: 140px; }
  .cell-role { width: 80px; }
  .cell-action { width: 40px; margin-left: auto; }

  @keyframes pulse {
    0%, 100% { opacity: 0.8; }
    50% { opacity: 0.4; }
  }
</style>
